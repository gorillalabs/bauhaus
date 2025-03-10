(ns gorillalabs.bauhaus.http.auth
  (:require [buddy.auth :as auth]
            [buddy.auth.backends :as backends]
            [gorillalabs.bauhaus.http.problem :as problem]
            [taoensso.timbre :as log]
            [com.github.sikt-no.clj-jwt :as clj-jwt]
            [integrant.core :as ig]
            [buddy.auth.accessrules :refer (success error)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; JWT claims handling (specific to Azure AD)
(defn principal [req]
  (let [id (:identity req)]
    ;; see https://docs.microsoft.com/en-us/azure/active-directory/develop/access-tokens
    ;; return the oid (user id) or, if no user id is given, the azp (The application ID typically represents an
    ;; application object, but it can also represent a service principal object in Azure AD.)
    (get id :oid (:azp id))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Authentication

(defn check-audience [valid-audiences access-token]
  (when-not (valid-audiences (:aud access-token))
    (throw (ex-info "Token has no valid audience" {:aud (:aud access-token)})))
  access-token)


;; TODO: https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-jwt-authorizer.html
(defn validate [valid-audiences access-token]
  (->>
    (clj-jwt/unsign "https://login.microsoftonline.com/common/discovery/keys"
                    access-token)
    ;; :exp is automatically checked with unsign.
    ;; Execution error (ExceptionInfo) at buddy.sign.jwt/validate-claims (jwt.clj:67).
    ;; Token is expired (1645116261)
    (check-audience valid-audiences)))


(defn ad-authfn
  "function to authenticate the incoming token and return an identity instance"
  [valid-audiences request token]
  (log/debug "auth")
  (try (validate valid-audiences token)
       (catch Throwable t
         (log/warn t "Cannot validate token" :request request)
         nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Authorization

;; This `authorization-problem-handler` is called whenever a handler is using
;; (buddy.auth/throw-unauthorized errordata)
;; or whenever a buddy rule fails

(defn authorization-problem-handler [request errordata]
  (log/debug "unauthorized" :request request :errordata errordata)
  (problem/response
    (if (auth/authenticated? request)
      (problem/problem "about:blank"
                       :status 403
                       :title "Forbidden"
                       :detail errordata)
      (problem/problem "about:blank"
                       :status 401
                       :title "Unauthorized"
                       :detail errordata))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; buddy rules handlers (see https://funcool.github.io/buddy-auth/latest/user-guide.html#rules-handlers)

(defn allow-unauthenticated
  [_]
  true)

(defn authenticated
  [request]
  (log/debug "Try to authenticate request" {:identity (:identity request)})
  (if (:identity request)
    true
    (error "Only authenticated users allowed")))

(defn aud?
  [valid-audiences request]
  (let [valid? (valid-audiences (:aud (:identity request)))]
    (when-not valid?
      (log/warn "Attempted unauthorized request"
                :valid-audiences valid-audiences
                :tags [:security]
                :identity (:identity request)
                :request request))
    valid?))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Create an instance
(defn- init [valid-audiences]
  (when-not valid-audiences
    (throw (problem/problem "misconfiguration"
                            :status 500
                            :title "No valid audiences"
                            :detail "Auth backend cannot start without valid audiences to check JWT claim :aud. Please check your system configuration."))
    )
  (backends/token {:authfn               (partial ad-authfn valid-audiences)
                   :unauthorized-handler authorization-problem-handler
                   :token-name           "Bearer"}))

(defmethod ig/init-key :gorillalabs.bauhaus.http-server/auth-backend [_ {:keys [valid-audiences]}]
  (log/debug "Setting up Entra ID token validation backend." :valid-audiences valid-audiences)
  (init valid-audiences))

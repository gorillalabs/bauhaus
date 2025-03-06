(ns gorillalabs.bauhaus.auth.identity.authenticate)

;; API namespace. This is very thin right now and directly tied to the
;; Active Directory / Entra-ID implementation, but can be basis for future extension.

(defprotocol IdentityProvider
  (acquire-token [provider scopes]))


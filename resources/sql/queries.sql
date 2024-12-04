-- :name save-message! :! :n
-- :doc creates a new message
INSERT INTO guestbook
(name, message)
VALUES (:name, :message)

-- :name list-messages :? :*
-- :doc selects all available messages
SELECT * FROM guestbook

-- :name get-message-by-id :? :1
-- :doc select message by id
SELECT * from guestbook WHERE id = :id

-- :name update-message :! :n
-- :doc update message by id
UPDATE guestbook
SET name=:name, message=:message
WHERE id=:id

-- :name create-guest! :! :n
-- :doc creates a new guest
INSERT INTO guests
(username, password)
VALUES (:username, :password)


-- :name list-guests :? :*
-- :doc selects all available guests
SELECT * FROM guests

-- :name get-guest-by-id :? :1
-- :doc select guest by id
SELECT * from guests WHERE id = :id

-- :name update-guest :! :n
-- :doc update guest by id
UPDATE guests
SET password=:password
WHERE id=:id

-- :name login-guest :? :1
-- :doc login guest by username & pqassword
SELECT id from guests WHERE username = :username AND password = :password

-- :name valid-user? :? :1
-- :doc valid user?
SELECT COUNT(*) frpm guests where id = :user-id
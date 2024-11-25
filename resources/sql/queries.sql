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
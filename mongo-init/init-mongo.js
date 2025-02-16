db.createUser({
    user: "chat_user",
    pwd: "password",
    roles: [ { role: "readWrite", db: "chat_db" } ]
});
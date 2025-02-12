const mysql = require('mysql2');

const host = process.env.DB_HOST;
const user = process.env.MYSQL_API_USER;
const database = process.env.DB_NAME;
const con = mysql.createConnection({
    host: host,
    user: user,
    database: database,
    password: process.env.MYSQL_API_PASSWORD
})

function query(sql, callback) {
    con.connect( (err) => {
        if (err) return callback(undefined, err);
        console.log(`Connected to ${host} as ${user}`);
    });
    con.query(sql, (err, res) => {
        if (err) {
            console.log(JSON.stringify(err));
            return callback(undefined, err);
        }
        console.log(JSON.stringify({
            query: sql,
            result: res
        }));
        callback(res);
    });
}

module.exports.query = query;

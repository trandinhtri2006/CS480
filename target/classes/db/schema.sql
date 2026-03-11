
PRAGMA foreign_keys = ON;


-- USERS

CREATE TABLE IF NOT EXISTS users (
    user_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    password_salt   VARCHAR(255) NOT NULL
);


-- FAVORITE ROUTES
-- - max 5 favorites per user enforced with trigger


CREATE TABLE IF NOT EXISTS favorite_routes (
    route_id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id                    INTEGER NOT NULL,
    favorite_name              VARCHAR(255) NOT NULL,
    origin_address             VARCHAR(255) NOT NULL,
    destination_address        VARCHAR(255) NOT NULL,
    chosen_route_index         INTEGER NOT NULL CHECK (chosen_route_index BETWEEN 0 AND 2),
    chosen_overview_polyline   TEXT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,

    CHECK (LENGTH(favorite_name) <= 255),
    CHECK (LENGTH(origin_address) >= 1),
    CHECK (LENGTH(destination_address) >= 1)
);


-- ROUTE WAYPOINTS
-- - each favorite route can have 0 to 5 waypoints
-- - order matters, so stop_order is usd 

CREATE TABLE IF NOT EXISTS route_waypoints (
    waypoint_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    route_id            INTEGER NOT NULL,
    stop_order          INTEGER NOT NULL CHECK (stop_order BETWEEN 1 AND 5),
    waypoint_address    VARCHAR(255) NOT NULL,

    FOREIGN KEY (route_id) REFERENCES favorite_routes(route_id) ON DELETE CASCADE,

    UNIQUE(route_id, stop_order),
    CHECK (LENGTH(waypoint_address) >= 1)
);


-- INDEXES

CREATE INDEX IF NOT EXISTS idx_users_email
    ON users(email);

CREATE INDEX IF NOT EXISTS idx_favorite_routes_user_id
    ON favorite_routes(user_id);

CREATE INDEX IF NOT EXISTS idx_route_waypoints_route_id
    ON route_waypoints(route_id);


-- TRIGGER: limit each user to 5 favorite routes

CREATE TRIGGER IF NOT EXISTS trg_limit_favorites_per_user
BEFORE INSERT ON favorite_routes
FOR EACH ROW
WHEN (
    SELECT COUNT(*)
    FROM favorite_routes
    WHERE user_id = NEW.user_id
) >= 5
BEGIN
    SELECT RAISE(ABORT, 'Maximum 5 favorite routes allowed per user.');
END;
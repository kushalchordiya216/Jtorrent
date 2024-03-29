DROP TABLE IF EXISTS Files;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users
(
    username  VARCHAR(100) NOT NULL,
    currentIP VARCHAR(100) DEFAULT NULL,
    password  VARCHAR(100) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    active    tinyint,
    UNIQUE (currentIP),
    PRIMARY KEY (username),
    UNIQUE KEY (username, currentIP),
    UNIQUE KEY (username, active),
    UNIQUE KEY (username, password),
    UNIQUE KEY (nickname, username)
);

CREATE TABLE Files
(
    merkleRoot VARCHAR(256) NOT NULL,
    filename VARCHAR(100) DEFAULT NULL,
    fiesizeMB INTEGER(10) default NULL,
    username   VARCHAR(100) NOT NULL,
    currentIP  VARCHAR(100) NOT NULL,
    active tinyint NOT NULL,
    foreign key (username, currentIP) references Users (username, currentIP) on update cascade on delete cascade,
    foreign key (username, active) references Users(username, active) on update cascade on delete cascade,
    unique key (username, merkleRoot)
);

/*
CREATE TABLE Users
(
    username  VARCHAR(20) NOT NULL,
    currentIP VARCHAR(20) DEFAULT NULL,
    password  VARCHAR(20) NOT NULL,
    active    tinyint,
    UNIQUE (currentIP),
    PRIMARY KEY (username),
    UNIQUE KEY (username, currentIP),
    UNIQUE KEY (username, active),
    UNIQUE KEY (username, password)
)

CREATE TABLE Files
(
    merkleRoot VARCHAR(32) not null,
    filename VARCHAR(50) NOT NULL,
    fiesizeMB INTEGER(10) default NULL,
    username   VARCHAR(20) not null,
    currentIP  VARCHAR(20) not null,
    active tinyint not null,
    foreign key (username, currentIP) references Users (username, currentIP) on update cascade,
    foreign key (username, active) references Users(username, active) on update cascade
)

CREATE INDEX merkleRoots
    ON Files (merkleRoot)

*/
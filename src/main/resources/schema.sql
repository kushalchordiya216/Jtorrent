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
    merkleRoot VARCHAR(32),
    fileSizeMB INTEGER(10),
    PRIMARY KEY(merkleRoot)
)


CREATE TABLE FileOwners
(
    merkleRoot VARCHAR(32) not null,
    username   VARCHAR(20) not null,
    currentIP  VARCHAR(20) not null,
    active tinyint not null,
    foreign key (username, currentIP) references Users (username, currentIP) on update cascade,
    foreign key (username, active) references Users(username, active) on update cascade,
    foreign key (merkleRoot) references Files(merkleRoot)
)


CREATE INDEX merkleRoots
    ON FileOwners (merkleRoot)
*/

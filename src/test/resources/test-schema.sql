CREATE TABLE link (id INT PRIMARY KEY auto_increment, name varchar(16) , 
url varchar(128) ,tags varchar(128)) ;


CREATE TABLE category (id INT PRIMARY KEY  auto_increment, name varchar(16) NOT NULL) ;


CREATE TABLE linkcategory (linkid INT NOT NULL, categoryid INT NOT NULL, PRIMARY KEY(linkid, categoryid) ) ;

CREATE TABLE account (accountid INT PRIMARY KEY auto_increment, 
siteName varchar(64),
siteUrl varchar(128),
userName varchar(32),
password varchar(128),
email varchar(128)) ) ;

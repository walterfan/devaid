/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * 
 * refer to http://www.ibm.com/developerworks/java/library/j-genericdao/index.html
 */
package com.github.walterfan.devaid.dao;

import java.io.Serializable;

/**
 *
 * @author walter
 */
public interface GenericDao <T, PK extends Serializable> {

    /** Persist the newInstance object into database */
    PK create(T newInstance);

    /** Retrieve an object that was previously persisted to the database using
     *   the indicated id as primary key
     */
    T read(PK id);

    /** Save changes made to a persistent object.  */
    void update(T transientObject);

    /** Remove an object from persistent storage in the database */
    void delete(T persistentObject);
}

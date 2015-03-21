/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.walterfan.devaid.dao;

import java.io.Serializable;

/**
 *
 * @author walter
 */
public class GenericDaoHibernateImpl <T, PK extends Serializable>
    implements GenericDao<T, PK> {
    private Class<T> type;

    
}
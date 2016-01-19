/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controller;

import com.ec.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.ec.entidades.DetalleRubro;
import com.ec.entidades.Rubro;
import com.ec.entidades.RubroFacruea;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author david
 */
public class RubroJpaController implements Serializable {

    public RubroJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Rubro rubro) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleRubro idDetalleRubro = rubro.getIdDetalleRubro();
            if (idDetalleRubro != null) {
                idDetalleRubro = em.getReference(idDetalleRubro.getClass(), idDetalleRubro.getIdDetalleRubro());
                rubro.setIdDetalleRubro(idDetalleRubro);
            }
            RubroFacruea idRubroFacrura = rubro.getIdRubroFacrura();
            if (idRubroFacrura != null) {
                idRubroFacrura = em.getReference(idRubroFacrura.getClass(), idRubroFacrura.getIdRubroFacrura());
                rubro.setIdRubroFacrura(idRubroFacrura);
            }
            em.persist(rubro);
            if (idDetalleRubro != null) {
                idDetalleRubro.getRubroCollection().add(rubro);
                idDetalleRubro = em.merge(idDetalleRubro);
            }
            if (idRubroFacrura != null) {
                idRubroFacrura.getRubroCollection().add(rubro);
                idRubroFacrura = em.merge(idRubroFacrura);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Rubro rubro) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rubro persistentRubro = em.find(Rubro.class, rubro.getIdRubro());
            DetalleRubro idDetalleRubroOld = persistentRubro.getIdDetalleRubro();
            DetalleRubro idDetalleRubroNew = rubro.getIdDetalleRubro();
            RubroFacruea idRubroFacruraOld = persistentRubro.getIdRubroFacrura();
            RubroFacruea idRubroFacruraNew = rubro.getIdRubroFacrura();
            if (idDetalleRubroNew != null) {
                idDetalleRubroNew = em.getReference(idDetalleRubroNew.getClass(), idDetalleRubroNew.getIdDetalleRubro());
                rubro.setIdDetalleRubro(idDetalleRubroNew);
            }
            if (idRubroFacruraNew != null) {
                idRubroFacruraNew = em.getReference(idRubroFacruraNew.getClass(), idRubroFacruraNew.getIdRubroFacrura());
                rubro.setIdRubroFacrura(idRubroFacruraNew);
            }
            rubro = em.merge(rubro);
            if (idDetalleRubroOld != null && !idDetalleRubroOld.equals(idDetalleRubroNew)) {
                idDetalleRubroOld.getRubroCollection().remove(rubro);
                idDetalleRubroOld = em.merge(idDetalleRubroOld);
            }
            if (idDetalleRubroNew != null && !idDetalleRubroNew.equals(idDetalleRubroOld)) {
                idDetalleRubroNew.getRubroCollection().add(rubro);
                idDetalleRubroNew = em.merge(idDetalleRubroNew);
            }
            if (idRubroFacruraOld != null && !idRubroFacruraOld.equals(idRubroFacruraNew)) {
                idRubroFacruraOld.getRubroCollection().remove(rubro);
                idRubroFacruraOld = em.merge(idRubroFacruraOld);
            }
            if (idRubroFacruraNew != null && !idRubroFacruraNew.equals(idRubroFacruraOld)) {
                idRubroFacruraNew.getRubroCollection().add(rubro);
                idRubroFacruraNew = em.merge(idRubroFacruraNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rubro.getIdRubro();
                if (findRubro(id) == null) {
                    throw new NonexistentEntityException("The rubro with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rubro rubro;
            try {
                rubro = em.getReference(Rubro.class, id);
                rubro.getIdRubro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rubro with id " + id + " no longer exists.", enfe);
            }
            DetalleRubro idDetalleRubro = rubro.getIdDetalleRubro();
            if (idDetalleRubro != null) {
                idDetalleRubro.getRubroCollection().remove(rubro);
                idDetalleRubro = em.merge(idDetalleRubro);
            }
            RubroFacruea idRubroFacrura = rubro.getIdRubroFacrura();
            if (idRubroFacrura != null) {
                idRubroFacrura.getRubroCollection().remove(rubro);
                idRubroFacrura = em.merge(idRubroFacrura);
            }
            em.remove(rubro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Rubro> findRubroEntities() {
        return findRubroEntities(true, -1, -1);
    }

    public List<Rubro> findRubroEntities(int maxResults, int firstResult) {
        return findRubroEntities(false, maxResults, firstResult);
    }

    private List<Rubro> findRubroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rubro.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Rubro findRubro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rubro.class, id);
        } finally {
            em.close();
        }
    }

    public int getRubroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rubro> rt = cq.from(Rubro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

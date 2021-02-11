/*
 *    Copyright [2015] [wisemapping]
 *
 *   Licensed under WiseMapping Public License, Version 1.0 (the "License").
 *   It is basically the Apache License, Version 2.0 (the "License") plus the
 *   "powered by wisemapping" text requirement on every single page;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the license at
 *
 *       http://www.wisemapping.org/license
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wisemapping.dao;

import com.wisemapping.model.*;
import com.wisemapping.util.ZipUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MindmapManagerImpl
        extends HibernateDaoSupport
        implements MindmapManager {

    @Override
    public Collaborator findCollaborator(@NotNull final String email) {
        final Collaborator collaborator;
        Query query = currentSession().createQuery("from com.wisemapping.model.Collaborator collaborator where email=:email");
        query.setParameter("email", email);

        final List<Collaborator> collaborators = query.list();
        if (collaborators != null && !collaborators.isEmpty()) {
            assert collaborators.size() == 1 : "More than one user with the same email!";
            collaborator = collaborators.get(0);
        } else {
            collaborator = null;
        }
        return collaborator;
    }

    @Override
    public List<Mindmap> search(MindMapCriteria criteria) {
        return search(criteria, -1);
    }

    @Override
    public List<MindMapHistory> getHistoryFrom(int mindmapId) {
        final Criteria hibernateCriteria = currentSession().createCriteria(MindMapHistory.class);
        hibernateCriteria.add(Restrictions.eq("mindmapId", mindmapId));
        hibernateCriteria.addOrder(Order.desc("creationTime"));

        // This line throws errors in some environments, so getting all history and taking firsts 10 records
        hibernateCriteria.setMaxResults(30);
        return hibernateCriteria.list();
    }

    @Override
    public MindMapHistory getHistory(int historyId) {
        return getHibernateTemplate().get(MindMapHistory.class, historyId);
    }

    @Override
    public void updateCollaboration(@NotNull Collaboration collaboration) {
        getHibernateTemplate().save(collaboration);
    }

    @Override
    public void purgeHistory(int mapId) throws IOException {
        final Criteria hibernateCriteria = currentSession().createCriteria(MindMapHistory.class);
        hibernateCriteria.add(Restrictions.eq("mindmapId", mapId));
        hibernateCriteria.addOrder(Order.desc("creationTime"));

        final List<MindMapHistory> historyList = hibernateCriteria.list();

        final Mindmap mindmap = this.getMindmapById(mapId);
        if (mindmap != null) {
            final Calendar yearAgo = Calendar.getInstance();
            yearAgo.add(Calendar.MONTH, -12);

            // If the map has not been modified in the last months, it means that I don't need to keep all the history ...
            int max = mindmap.getLastModificationTime().before(yearAgo) ? 10 : 25;

            final HibernateTemplate hibernateTemplate = getHibernateTemplate();
            for (MindMapHistory history : historyList) {
                byte[] zippedXml = history.getZippedXml();
                if (new String(zippedXml).startsWith("<map")) {
                    history.setZippedXml(ZipUtils.bytesToZip(zippedXml));
                    hibernateTemplate.update(history);
                }
            }

            if (historyList.size() > max) {
                for (int i = max; i < historyList.size(); i++) {
                    hibernateTemplate.delete(historyList.get(i));
                }
            }
        }
    }

    @Override
    public List<Mindmap> search(MindMapCriteria criteria, int maxResult) {
        final Criteria hibernateCriteria = currentSession().createCriteria(Mindmap.class);
        //always search public maps
        hibernateCriteria.add(Restrictions.like("public", Boolean.TRUE));

        if (criteria != null) {
            final Junction junction;
            if (criteria.isOrCriteria()) {
                junction = Restrictions.disjunction();
            } else {
                junction = Restrictions.conjunction();
            }

            if (criteria.getTitle() != null && criteria.getTitle().length() > 0) {
                final SimpleExpression titleRestriction = Restrictions.like("title", "%" + criteria.getTitle() + "%");
                junction.add(titleRestriction);
            }

            if (criteria.getDescription() != null && criteria.getDescription().length() > 0) {
                final SimpleExpression descriptionRestriction = Restrictions.like("description", "%" + criteria.getDescription() + "%");
                junction.add(descriptionRestriction);
            }
            if (criteria.getTags().size() > 0) {
                for (String tag : criteria.getTags()) {
                    final SimpleExpression tagRestriction = Restrictions.like("tags", "%" + tag + "%");
                    junction.add(tagRestriction);
                }
            }

            hibernateCriteria.add(junction);
        }
        return hibernateCriteria.list();
    }

    @Override
    public Collaborator findCollaborator(long id) {
        return getHibernateTemplate().get(Collaborator.class, id);
    }

    @Override
    public List<Collaboration> findCollaboration(final long collaboratorId) {
        Query query = currentSession().createQuery("from com.wisemapping.model.Collaboration collaboration where colaborator_id=:colaboratorId");
        query.setParameter("colaboratorId", collaboratorId);
        return query.list();
    }

    @Override
    public List<Collaboration> findCollaboration(final CollaborationRole collaborationRole) {
        Query query = currentSession().createQuery("from com.wisemapping.model.Collaboration collaboration where roleId=:roleId");
        query.setParameter("roleId", collaborationRole.ordinal());
        return query.list();
    }

    @Override
    public Collaboration findCollaboration(final int mindmapId, final User user) {
        final Collaboration result;

        Query query = currentSession().createQuery("from com.wisemapping.model.Collaboration collaboration where mindMap.id=:mindMapId and colaborator_id=:collaboratorId");
        query.setParameter("mindMap", mindmapId);
        query.setParameter("collaboratorId", user.getId());

        final List<Collaboration> mindMaps = query.list();

        if (mindMaps != null && !mindMaps.isEmpty()) {
            result = mindMaps.get(0);
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public void addCollaborator(@NotNull Collaborator collaborator) {
        assert collaborator != null : "ADD MINDMAP COLLABORATOR: Collaborator is required!";
        getHibernateTemplate().save(collaborator);
    }

    @Override
    public void removeCollaboration(Collaboration collaboration) {
        getHibernateTemplate().delete(collaboration);
    }

    @Override
    public void removeCollaborator(@NotNull Collaborator collaborator) {
        getHibernateTemplate().delete(collaborator);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Mindmap> getAllMindmaps() {
        return currentSession().createQuery("from com.wisemapping.model.Mindmap wisemapping").list();
    }

    @Override
    @Nullable
    public Mindmap getMindmapById(int id) {
        return getHibernateTemplate().get(Mindmap.class, id);
    }

    @Override
    public Mindmap getMindmapByTitle(final String title, final User user) {
        final Mindmap result;
        Query query = currentSession().createQuery("from com.wisemapping.model.Mindmap wisemapping where title=:title and creator=:creator");
        query.setParameter("title", title);
        query.setParameter("creator", user);

        List<Mindmap> mindMaps = query.list();

        if (mindMaps != null && !mindMaps.isEmpty()) {
            result = mindMaps.get(0);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public void addMindmap(User user, Mindmap mindMap) {
        saveMindmap(mindMap);
    }

    @Override
    public void saveMindmap(Mindmap mindMap) {
        assert mindMap != null : "Save Mindmap: Mindmap is required!";
        currentSession().save(mindMap);
    }

    @Override
    public void updateMindmap(@NotNull Mindmap mindMap, boolean saveHistory) {
        assert mindMap != null : "Save Mindmap: Mindmap is required!";
        getHibernateTemplate().saveOrUpdate(mindMap);
        if (saveHistory) {
            saveHistory(mindMap);
        }
    }

    @Override
    public void removeMindmap(@NotNull final Mindmap mindMap) {
        // Delete history first ...
        final Criteria hibernateCriteria = currentSession().createCriteria(MindMapHistory.class);
        hibernateCriteria.add(Restrictions.eq("mindmapId", mindMap.getId()));
        List list = hibernateCriteria.list();
        getHibernateTemplate().deleteAll(list);

        // Delete mindmap ....
        getHibernateTemplate().delete(mindMap);
    }

    private void saveHistory(@NotNull final Mindmap mindMap) {
        final MindMapHistory history = new MindMapHistory();

        history.setZippedXml(mindMap.getZippedXml());
        history.setCreationTime(Calendar.getInstance());
        history.setEditor(mindMap.getLastEditor());
        history.setMindmapId(mindMap.getId());
        getHibernateTemplate().saveOrUpdate(history);
    }
}

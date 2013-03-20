/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import net.pickapack.model.WithCreateTime;
import net.pickapack.model.WithId;
import net.pickapack.model.WithParentId;
import net.pickapack.model.WithTitle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Abstract service.
 *
 * @author Min Cai
 */
public class AbstractService implements Service {
    private JdbcPooledConnectionSource connectionSource;

    /**
     * Create an abstract service.
     *
     * @param databaseUrl database URL
     * @param dataClasses a list of data classes
     */
    public AbstractService(String databaseUrl, List<Class<? extends WithId>> dataClasses) {
        try {
            this.connectionSource = new JdbcPooledConnectionSource(databaseUrl);
            this.connectionSource.setCheckConnectionsEveryMillis(0);
            this.connectionSource.setTestBeforeGet(true);

            for (Class<?> dataClz : dataClasses) {
                TableUtils.createTableIfNotExists(this.connectionSource, dataClz);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stop the service.
     */
    public void stop() {
        try {
            this.connectionSource.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the items.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @return the items
     */
    public <TItem extends WithId> List<TItem> getItems(Dao<TItem, Long> dao) {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the items with paging.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @param offset the offset
     * @param limit the maximum number of items to be fetched
     * @return the items with the specified offset and limit
     */
    public <TItem extends WithId> List<TItem> getItems(Dao<TItem, Long> dao, long offset, long limit) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().offset(offset).limit(limit).prepare();
            return dao.query(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the number of items.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @return the number of items
     */
    public <TItem extends WithId> long getNumItems(Dao<TItem, Long> dao) {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the items under the specified parent.
     *
     * @param <TItem> the item type
     * @param <TItemDirectory> the item directory type
     * @param dao the data access object
     * @param parent the parent
     * @return the items under the specified parent
     */
    public <TItem extends WithParentId, TItemDirectory extends WithId> List<TItem> getItemsByParent(Dao<TItem, Long> dao, TItemDirectory parent) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().where().eq("parentId", parent.getId()).prepare();
            return dao.query(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the items under the specified parent with paging.
     *
     * @param <TItem> the item type
     * @param <TItemDirectory> the item directory type
     * @param dao the data access object
     * @param parent the parent
     * @param offset the offset
     * @param limit the limit
     * @return the items under the specified parent with the specified offset and limit
     */
    public <TItem extends WithParentId, TItemDirectory extends WithId> List<TItem> getItemsByParent(Dao<TItem, Long> dao, TItemDirectory parent, long offset, long limit) {
        try {
            QueryBuilder<TItem, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.offset(offset).limit(limit);
            queryBuilder.where().eq("parentId", parent.getId());
            PreparedQuery<TItem> query = queryBuilder.prepare();
            return dao.query(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the number of items under the specified parent.
     *
     * @param <TItem> the item type
     * @param <TItemDirectory> the item directory type
     * @param dao the data access object
     * @param parent the parent
     * @return the number of items under the specified parent
     */
    public <TItem extends WithParentId, TItemDirectory extends WithId> long getNumItemsByParent(Dao<TItem, Long> dao, TItemDirectory parent) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().setCountOf(true).where().eq("parentId", parent.getId()).prepare();
            return dao.countOf(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the item matching the specified ID.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @param id the ID
     * @return the item matching the specified ID
     */
    public <TItem extends WithId> TItem getItemById(Dao<TItem, Long> dao, long id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the first item matching the specified title.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @param title the title
     * @return the first item matching the specified title
     */
    public <TItem extends WithTitle> TItem getFirstItemByTitle(Dao<TItem, Long> dao, String title) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().where().eq("title", title).prepare();
            return dao.queryForFirst(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the first item under the specified parent.
     *
     * @param <TItem> the item type
     * @param <TItemDirectory> the item directory type
     * @param dao the data access object
     * @param parent the parent
     * @return the first item under the specified parent
     */
    public <TItem extends WithParentId, TItemDirectory extends WithId> TItem getFirstItemByParent(Dao<TItem, Long> dao, TItemDirectory parent) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().where().eq("parentId", parent.getId()).and().prepare();
            return dao.queryForFirst(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the latest item matching the specified title.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @param title the  title
     * @return the latest item matching the specified title
     */
    public <TItem extends WithTitle & WithCreateTime> TItem getLatestItemByTitle(Dao<TItem, Long> dao, String title) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().orderBy("createTime", false).where().eq("title", title).prepare();
            return dao.queryForFirst(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the list of items matching the specified title.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @param title the title
     * @return the list of items matching the specified title
     */
    public <TItem extends WithTitle> List<TItem> getItemsByTitle(Dao<TItem, Long> dao, String title) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().where().eq("title", title).prepare();
            return dao.query(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the list of items matching the specified title and with paging.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @param title the title
     * @param offset the offset
     * @param limit the limit
     * @return the list of items matching the specified title and with the specified offset and limit
     */
    public <TItem extends WithTitle> List<TItem> getItemsByTitle(Dao<TItem, Long> dao, String title, long offset, long limit) {
        try {
            QueryBuilder<TItem, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("title", title);
            queryBuilder.offset(offset).limit(limit);
            return dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the number of items matching the specified title.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @param title the title
     * @return the number of items matching the specified title
     */
    public <TItem extends WithTitle> long getNumItemsByTitle(Dao<TItem, Long> dao, String title) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().setCountOf(true).where().eq("title", title).prepare();
            return dao.countOf(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the first item.
     *
     * @param <TItem> the item type
     * @param dao the data access object
     * @return the first item if any exists; otherwise null
     */
    public <TItem extends WithId> TItem getFirstItem(Dao<TItem, Long> dao) {
        try {
            PreparedQuery<TItem> query = dao.queryBuilder().prepare();
            return dao.queryForFirst(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add an item.
     *
     * @param dao the data access object
     * @param item the item that is to be added
     * @return the ID of the newly added item
     */
    public <TItem extends WithId> long addItem(final Dao<TItem, Long> dao, final TItem item) {
        addItems(dao, new ArrayList<TItem>() {{
            add(item);
        }});
        return item.getId();
    }

    /**
     * Add a list of items.
     *
     * @param dao the data access object
     * @param items the list of items that is to be added
     */
    public <TItem extends WithId> void addItems(final Dao<TItem, Long> dao, final List<TItem> items) {
        try {
            TransactionManager.callInTransaction(getConnectionSource(),
                    new Callable<Void>() {
                        public Void call() throws Exception {
                            for (TItem item : items) {
                                dao.create(item);
                            }
                            return null;
                        }
                    });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove the item matching the specified ID.
     *
     * @param dao the data access object
     * @param id the ID of the item that is to be removed
     */
    public <TItem extends WithId> void removeItemById(Dao<TItem, Long> dao, final long id) {
        removeItemsByIds(dao, new ArrayList<Long>(){{
            add(id);
        }});
    }

    /**
     * Remove the specified items.
     *
     * @param dao the data access object
     * @param items the items that is to be removed
     */
    public <TItem extends WithId> void removeItems(Dao<TItem, Long> dao, final List<TItem> items) {
        removeItemsByIds(dao, new ArrayList<Long>(){{
            for (TItem item : items) {
                add(item.getId());
            }
        }});
    }

    /**
     * Remove the items matching the specified list of IDs.
     *
     * @param dao the data access object
     * @param ids the list of IDs
     */
    public <TItem extends WithId> void removeItemsByIds(final Dao<TItem, Long> dao, final List<Long> ids) {
        try {
            DeleteBuilder<TItem,Long> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().in("id", ids);
            dao.delete(deleteBuilder.prepare());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clear the items.
     *
     * @param dao the data access object
     */
    public <TItem extends WithId> void clearItems(Dao<TItem, Long> dao) {
        try {
            dao.delete(dao.deleteBuilder().prepare());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update the specified item.
     *
     * @param dao the data access object
     * @param item the item that is to be updated
     */
    public <TItem extends WithId> void updateItem(Dao<TItem, Long> dao, final TItem item) {
        updateItems(dao, new ArrayList<TItem>(){{
            add(item);
        }});
    }

    /**
     * Update the specified list of items.
     *
     * @param dao the data access object
     * @param items the items that is to be updated
     */
    public <TItem extends WithId> void updateItems(final Dao<TItem, Long> dao, final List<TItem> items) {
        try {
            TransactionManager.callInTransaction(getConnectionSource(),
                    new Callable<Void>() {
                        public Void call() throws Exception {
                            for (TItem item : items) {
                                dao.update(item);
                            }

                            return null;
                        }
                    });


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a data access object for the specified class.
     *
     * @param <TItem> the item type
     * @param <D> the dao type
     * @param clz the class
     * @return the newly created data access object for the specified class
     */
    protected <TItem extends WithId, D extends Dao<TItem, Long>> D createDao(Class<TItem> clz) {
        try {
            return DaoManager.createDao(this.connectionSource, clz);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the JDBC connection source.
     *
     * @return the JDBC connection source
     */
    protected JdbcPooledConnectionSource getConnectionSource() {
        return connectionSource;
    }
}

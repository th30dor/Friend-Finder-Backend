package teo.friendidentifier;

import teo.friendidentifier.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "dataentityendpoint", namespace = @ApiNamespace(ownerDomain = "friendidentifier.teo", ownerName = "friendidentifier.teo", packagePath = ""))
public class DataEntityEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listDataEntity")
	public CollectionResponse<DataEntity> listDataEntity(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<DataEntity> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr
					.createQuery("select from DataEntity as DataEntity");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<DataEntity>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (DataEntity obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<DataEntity> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getDataEntity")
	public DataEntity getDataEntity(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		DataEntity dataentity = null;
		try {
			dataentity = mgr.find(DataEntity.class, id);
		} finally {
			mgr.close();
		}
		return dataentity;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param dataentity the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertDataEntity")
	public DataEntity insertDataEntity(DataEntity dataentity) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsDataEntity(dataentity)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(dataentity);
		} finally {
			mgr.close();
		}
		return dataentity;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param dataentity the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateDataEntity")
	public DataEntity updateDataEntity(DataEntity dataentity) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsDataEntity(dataentity)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(dataentity);
		} finally {
			mgr.close();
		}
		return dataentity;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeDataEntity")
	public void removeDataEntity(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		try {
			DataEntity dataentity = mgr.find(DataEntity.class, id);
			mgr.remove(dataentity);
		} finally {
			mgr.close();
		}
	}

	private boolean containsDataEntity(DataEntity dataentity) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			DataEntity item = mgr.find(DataEntity.class, dataentity.getId());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}

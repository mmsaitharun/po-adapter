package oneapp.workbox.poadapter.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import com.incture.pmc.poadapter.dto.BaseDto;
import com.incture.pmc.poadapter.entity.BaseDo;
import com.incture.pmc.poadapter.util.EnOperation;
import com.incture.pmc.poadapter.util.ExecutionFault;
import com.incture.pmc.poadapter.util.InvalidInputFault;
import com.incture.pmc.poadapter.util.MessageUIDto;
import com.incture.pmc.poadapter.util.NoResultFault;
import com.incture.pmc.poadapter.util.NonUniqueRecordFault;
import com.incture.pmc.poadapter.util.RecordExistFault;
import com.incture.pmc.poadapter.util.ServicesUtil;

/**
 * The <code>BaseDao</code> abstract class comprise abstract functions for CRUD
 * operations and a few utility functions for child <code>Data Access Objects
 * <code>
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
public abstract class BaseDao<E extends BaseDo, D extends BaseDto> {

	protected final boolean isNotQuery = false;
	protected final boolean isQuery = true;
	private final String recordExist = "Record already exist ";
	private final String noRecordFound = "No record found: ";

	private EntityManager entityManager;

	public BaseDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * @param dto
	 *            the record to be created
	 * @throws ExecutionFault
	 *             in case for fatal error
	 * @throws InvalidInputFault
	 *             wrong inputs
	 * @throws NoResultFault
	 */
	public void create(D dto) throws ExecutionFault, InvalidInputFault, NoResultFault {
		persist(importDto(EnOperation.CREATE, dto));
	}

	/**
	 * @param dto
	 *            input object
	 * @return single record based on the objects primary key
	 * @throws ExecutionFault
	 *             in case for fatal error
	 * @throws InvalidInputFault
	 *             even key is missing
	 * @throws NoResultFault
	 *             when record could be retrieved
	 */
	public D getByKeys(D dto) throws ExecutionFault, InvalidInputFault, NoResultFault {
		return exportDto(getByKeysForFK(dto));
	}

	/**
	 * @return the entity, mainly used for setting FK
	 */
	public E getByKeysForFK(D dto) throws ExecutionFault, InvalidInputFault, NoResultFault {
		return find(importDto(EnOperation.RETRIEVE, dto));
	}

	/**
	 * @param dto
	 *            the record to be updated
	 * @return the updated record
	 * @throws ExecutionFault
	 *             in case for fatal error
	 * @throws InvalidInputFault
	 *             wrong inputs
	 * @throws NoResultFault
	 */
	public D update(D dto) throws ExecutionFault, InvalidInputFault, NoResultFault {
		getByKeysForFK(dto);
		return exportDto(merge(importDto(EnOperation.UPDATE, dto)));
	}

	public void delete(D dto) throws ExecutionFault, InvalidInputFault, NoResultFault {
		remove(getByKeysForFK(dto));
	}

	protected void persist(E pojo) throws ExecutionFault {
		try {
			getEntityManager().persist(pojo);
			getEntityManager().flush();
		} catch (Exception e) {
			MessageUIDto faultInfo = new MessageUIDto();
			faultInfo.setMessage(e.getMessage());
			String message = "Create of " + pojo.getClass().getSimpleName() + " with keys " + pojo.getPrimaryKey()
					+ " failed!";
			throw new ExecutionFault(message, faultInfo, e);
		}
	}

	@SuppressWarnings("unchecked")
	protected E find(E pojo) throws ExecutionFault, NoResultFault {
		E result = null;
		try {
			result = (E) getEntityManager().find(pojo.getClass(), pojo.getPrimaryKey());
		} catch (Exception e) {
			e.printStackTrace();
			MessageUIDto faultInfo = new MessageUIDto();
			faultInfo.setMessage(e.getMessage());
			String message = "Retrieve of " + pojo.getClass().getSimpleName() + " by keys " + pojo.getPrimaryKey()
					+ " failed!";
			throw new ExecutionFault(message, faultInfo, e);
		}
		if (result == null) {
			throw new NoResultFault(noRecordFound + pojo.getClass().getSimpleName() + "#" + pojo.getPrimaryKey());
		}
		return result;
	}

	protected E merge(E pojo) throws ExecutionFault {
		try {
			return getEntityManager().merge(pojo);
		} catch (Exception e) {
			MessageUIDto faultInfo = new MessageUIDto();
			faultInfo.setMessage(e.getMessage());
			String message = "Update of " + pojo.getClass().getSimpleName() + " having keys " + pojo.getPrimaryKey()
					+ " failed!";
			throw new ExecutionFault(message, faultInfo, e);
		}
	}

	protected void remove(E pojo) throws ExecutionFault {
		try {
			getEntityManager().remove(pojo);
		} catch (Exception e) {
			MessageUIDto faultInfo = new MessageUIDto();
			faultInfo.setMessage(e.getMessage());
			String message = "Delete of " + pojo.getClass().getSimpleName() + " having keys " + pojo.getPrimaryKey()
					+ " failed!";
			throw new ExecutionFault(message, faultInfo, e);
		}
	}

	// </BASIC CRUD OPERATIONS>
	// <SIGNATURE FOR DATA TRANSFER FUNCTIONS>
	private E importDto(EnOperation operation, D fromDto) throws InvalidInputFault, ExecutionFault, NoResultFault {
		if (fromDto != null) {
			fromDto.validate(operation);
			return importDto(fromDto);
		}
		throw new InvalidInputFault("Empty DTO passed");
	}

	/**
	 * @param fromDto
	 *            Data object from which data needs to be copied to a new entity
	 */
	protected abstract E importDto(D fromDto) throws InvalidInputFault, ExecutionFault, NoResultFault;

	/**
	 * @param entity
	 *            Copies data back to a new transfer object from entity
	 */
	protected abstract D exportDto(E entity);

	protected List<D> exportDtoList(Collection<E> listDo) {
		List<D> returnDtos = null;
		if (!ServicesUtil.isEmpty(listDo)) {
			returnDtos = new ArrayList<D>(listDo.size());
			for (Iterator<E> iterator = listDo.iterator(); iterator.hasNext();) {
				returnDtos.add(exportDto(iterator.next()));
			}
		}
		return returnDtos;
	}

	/**
	 * Its negation logic over getByKeys.
	 * 
	 * @param dto
	 * @throws ExecutionFault
	 * @throws RecordExistFault
	 * @throws InvalidInputFault
	 */
	protected void entityExist(D dto) throws ExecutionFault, RecordExistFault, InvalidInputFault {
		try {// Report entity exist
			getByKeys(dto);
			throw new RecordExistFault(recordExist, buildRecordExistFault(dto));
		} catch (NoResultFault e) {
		}
	}

	protected final EntityManager getEntityManager() {
		return entityManager;
	}

	public final void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * 
	 * @param queryName
	 *            used for logging
	 * 
	 * @param query
	 *            object used for execution
	 * @param parameters
	 *            to be set in where clause
	 * @return Single record, depending on columns in SELECT clause, it return a
	 *         object of BaseDo type or an object array
	 * @throws NoResultFault
	 * @throws NonUniqueRecordFault
	 */

	protected Object getSingleResult(String queryName, Query query, Object... parameters)
			throws NoResultFault, NonUniqueRecordFault {
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			throw new NoResultFault(ServicesUtil.buildNoRecordMessage(queryName, parameters));
		} catch (NonUniqueResultException e) {
			throw new NonUniqueRecordFault("Failed due to corrupt data, please contact db admin",
					buildNonUniqueRecordFault(queryName, parameters));
		}
	}

	/**
	 * @param queryName
	 *            used for logging
	 * @param query
	 *            object used for execution
	 * @param parameters
	 *            to be set in where clause
	 * @return List of records based on startIndex and batchIndex
	 * @throws NoResultFault
	 */
	protected List<?> getResultList(String queryName, Query query, Object... parameters) throws NoResultFault {
		List<?> returnList = query.getResultList();
		if (ServicesUtil.isEmpty(returnList)) {
			throw new NoResultFault(ServicesUtil.buildNoRecordMessage(queryName, parameters));
		}
		return returnList;
	}

	protected String uuidGen(BaseDto dto, Class<? extends BaseDo> classDo) throws ExecutionFault {
		return UUID.randomUUID().toString();
	}

	private MessageUIDto buildRecordExistFault(BaseDto BaseDto) {
		StringBuffer sb = new StringBuffer(recordExist);
		if (BaseDto != null) {
			sb.append(BaseDto.toString());
		}
		MessageUIDto messageUIDto = new MessageUIDto();
		messageUIDto.setMessage(sb.toString());
		return messageUIDto;
	}

	private MessageUIDto buildNonUniqueRecordFault(String queryName, Object... parameters) {
		StringBuffer sb = new StringBuffer("Non Unique Record found for query: ");
		sb.append(queryName);
		if (!ServicesUtil.isEmpty(parameters)) {
			sb.append(" for params:");
			sb.append(ServicesUtil.getCSV(parameters));
		}
		MessageUIDto messageUIDto = new MessageUIDto();
		messageUIDto.setMessage(sb.toString());
		return messageUIDto;
	}

	@SuppressWarnings("unchecked")
	public List<D> getAllResults(String doName, Object... parameters) throws NoResultFault {
		String queryName = "SELECT p FROM " + doName + " p";
		Query query = getEntityManager().createQuery(queryName);
		List<E> returnList = query.getResultList();
		if (ServicesUtil.isEmpty(returnList)) {
			throw new NoResultFault(ServicesUtil.buildNoRecordMessage(queryName, parameters));
		}
		return exportDtoList(returnList);
	}

	@SuppressWarnings("unchecked")
	public List<D> getAllActiveResults(String doName, String columnName, String value, Object... parameters)
			throws NoResultFault {
		String queryName = "SELECT p FROM " + doName + " p" + " where p." + columnName + " = '" + value + "' ";
		Query query = getEntityManager().createQuery(queryName);
		List<E> returnList = query.getResultList();
		return exportDtoList(returnList);
	}

	@SuppressWarnings("unchecked")
	public List<D> getAllActiveResults(String doName, String columnName, Integer value, Object... parameters) {
		String queryName = "SELECT p FROM " + doName + " p" + " where p." + columnName + "=" + value;
		Query query = getEntityManager().createQuery(queryName.toString());
		List<E> returnList = query.getResultList();
		return exportDtoList(returnList);
	}

	@SuppressWarnings("unchecked")
	public List<D> getSpecificActiveResults(String doName, String columnName, String value, Object... parameters)
			throws NoResultFault {
		String queryName = "SELECT p FROM " + doName + " p" + " where p." + columnName + " = :value";
		Query query = getEntityManager().createQuery(queryName);
		query.setParameter("value", value.trim());
		List<E> returnList = query.getResultList();
		if (ServicesUtil.isEmpty(returnList)) {
			throw new NoResultFault(ServicesUtil.buildNoRecordMessage(queryName, parameters));
		}
		return exportDtoList(returnList);
	}
}
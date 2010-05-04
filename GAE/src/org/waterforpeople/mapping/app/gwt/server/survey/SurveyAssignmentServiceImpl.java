package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.domain.SurveyAssignment;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service for assignment of surveys to devices
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAssignmentServiceImpl extends RemoteServiceServlet implements
		SurveyAssignmentService {
	private BaseDAO<SurveyAssignment> surveyAssignmentDao;
	private DeviceDAO deviceDao;
	private SurveyDAO surveyDao;
	private static final long serialVersionUID = 3956064184547647245L;
	private static final Logger logger = Logger
			.getLogger(SurveyAssignmentServiceImpl.class);

	public SurveyAssignmentServiceImpl() {
		surveyAssignmentDao = new BaseDAO<SurveyAssignment>(
				SurveyAssignment.class);
		deviceDao = new DeviceDAO();
		surveyDao = new SurveyDAO();
	}

	/**
	 * saves a surveyAssignment to the datastore
	 */
	@Override
	public void saveSurveyAssignment(SurveyAssignmentDto dto) {
		SurveyAssignment assignment = new SurveyAssignment();
		if (dto.getKeyId() != null) {
			try {

				BeanUtils.copyProperty(assignment, "Key.Id", dto.getKeyId());
			} catch (Exception e) {
				logger.error("Could not set key on survey assignment", e);
			}
		}
		if (dto.getDevices() != null) {
			List<Long> devIds = new ArrayList<Long>();
			for (DeviceDto dev : dto.getDevices()) {
				devIds.add(dev.getKeyId());
			}
			assignment.setDeviceIds(devIds);
		}
		if (dto.getSurveys() != null) {
			List<Long> surveyIds = new ArrayList<Long>();
			for (SurveyDto s : dto.getSurveys()) {
				surveyIds.add(s.getKeyId());
			}
			assignment.setSurveyIds(surveyIds);
		}
		surveyAssignmentDao.save(assignment);
		// TODO: also generate the device job queue objects		
	}

	/**
	 * lists all assignments
	 * TODO: move dto/domain conversion out
	 */
	@Override
	public SurveyAssignmentDto[] listSurveyAssignments() {
		List<SurveyAssignment> assignments = surveyAssignmentDao.list(null);
		SurveyAssignmentDto[] results = null;
		if (assignments != null) {
			results = new SurveyAssignmentDto[assignments.size()];
			for (int i = 0; i < assignments.size(); i++) {
				SurveyAssignmentDto dto = new SurveyAssignmentDto();
				dto.setKeyId(assignments.get(i).getKey().getId());
				if (assignments.get(i).getDeviceIds() != null) {
					ArrayList<DeviceDto> devices = new ArrayList<DeviceDto>();
					for (Long id : assignments.get(i).getDeviceIds()) {
						Device dev = deviceDao.getByKey(id);
						if (dev != null) {
							DeviceDto devDto = new DeviceDto();
							devDto.setPhoneNumber(dev.getPhoneNumber());
							devDto.setKeyId(dev.getKey().getId());
							devices.add(devDto);
						}
					}
					dto.setDevices(devices);
				}
				if (assignments.get(i).getSurveyIds() != null) {
					ArrayList<SurveyDto> surveys = new ArrayList<SurveyDto>();
					for (Long id : assignments.get(i).getSurveyIds()) {
						Survey survey = surveyDao.getByKey(id);
						if (survey != null) {
							SurveyDto sDto = new SurveyDto();
							sDto.setName(survey.getName());
							sDto.setKeyId(survey.getKey().getId());
							surveys.add(sDto);
						}
					}
					dto.setSurveys(surveys);
				}
				results[i] = dto;
			}
		}
		return results;
	}

}

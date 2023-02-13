package com.example.new_hr_system.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.new_hr_system.constants.AbsenceSystemRtnCode;
import com.example.new_hr_system.entity.AbsenceSystem;
import com.example.new_hr_system.entity.EmployeeInfo;
import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.respository.AbsenceSystemDao;
import com.example.new_hr_system.respository.EmployeeInfoDao;
import com.example.new_hr_system.respository.WorkSystemDao;
import com.example.new_hr_system.service.ifs.AbsenceSystemService;
import com.example.new_hr_system.vo.AbsenceSystemReq;
import com.example.new_hr_system.vo.AbsenceSystemRes;
import com.example.new_hr_system.vo.AbsenceSystemResList;
import com.example.new_hr_system.vo.EmployeeInfoRes;

@Service
public class AbsenceSystemServiceImpl implements AbsenceSystemService {

	@Autowired
	private AbsenceSystemDao absenceSystemDao;

	@Autowired
	private EmployeeInfoDao employeeInfoDao;

	@Autowired
	private WorkSystemDao workSystemDao;

	@Autowired
	private JavaMailSender mailSender;

	// �Ыؽа�����(�浧),�ñH�eemail�����D�ާ��
	@Override
	public AbsenceSystemRes addAbsence(AbsenceSystemReq req, HttpSession httpSession) {

		if (!StringUtils.hasText(req.getAbsenceReason())) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_REASON_REQOIRED.getMessage());
		}
		Object attValue = httpSession.getAttribute("employee_code");
		if (attValue == null) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
		}

		String employeeCode = attValue.toString();

		EmployeeInfo employee = employeeInfoDao.findById(employeeCode).get();

		if (!employee.getEmployeeCode().equalsIgnoreCase(employeeCode)) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
		}

		AbsenceSystem absence = new AbsenceSystem(UUID.randomUUID(), employeeCode, req.getAbsenceReason(),
				req.getAbsenceDate());

		absenceSystemDao.save(absence);

		EmployeeInfo employeeInfo = employeeInfoDao.findById(employeeCode).get();

		SimpleMailMessage gmail = new SimpleMailMessage();

		gmail.setFrom("kennymax22581997@gmail.com");
		gmail.setTo(req.getEmail());
		gmail.setSubject("�D���G�Z�H��ȹC�έ��u������");
		gmail.setText("���u: " + employeeInfo.getEmployeeCode() + " " + employeeInfo.getName() + " ������ݭn���" + "   �а����: "
				+ req.getAbsenceDate() + "   �ƥ�: " + req.getAbsenceStr());
		mailSender.send(gmail);

		return new AbsenceSystemRes(AbsenceSystemRtnCode.SUCCESSFUL.getMessage());
	}

	// �Ыؽа�����(�h��),�ñH�eemail�����D�ާ��
	@Override
	public AbsenceSystemRes addAbsences(AbsenceSystemReq req, HttpSession httpSession) {
		if (!StringUtils.hasText(req.getAbsenceReason())) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_REASON_REQOIRED.getMessage());
		}
		Object attValue = httpSession.getAttribute("employee_code");
		if (attValue == null) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
		}

		String employeeCode = attValue.toString();

		EmployeeInfo employee = employeeInfoDao.findById(employeeCode).get();

		if (!employee.getEmployeeCode().equalsIgnoreCase(employeeCode)) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
		}
		  long y = req.getAbsenceEndDate().toEpochDay() - req.getAbsenceStartDate().toEpochDay();
		
		List<AbsenceSystem> absenceList = new ArrayList<>();
		
		
		for(int x = 0; x <= y; x++ ) {
			AbsenceSystem absence = new AbsenceSystem(UUID.randomUUID(), employeeCode, req.getAbsenceReason(),
					req.getAbsenceStartDate().plusDays(x));
			absenceList.add(absence);
		}
		

		absenceSystemDao.saveAll(absenceList);

		SimpleMailMessage gmail = new SimpleMailMessage();

		gmail.setFrom("kennymax22581997@gmail.com");
		gmail.setTo(req.getEmail());
		gmail.setSubject("�D���G�Z�H��ȹC�έ��u������");
		gmail.setText("���u: " + employee.getEmployeeCode() + " " + employee.getName() + " ������ݭn���" + "   �а����: "
				+ req.getAbsenceStartDate() + "~" + req.getAbsenceEndDate() + "   �ƥ�: " + req.getAbsenceStr());
		mailSender.send(gmail);

		return new AbsenceSystemRes(AbsenceSystemRtnCode.SUCCESSFUL.getMessage());
	}

	// �R������
	@Override
	public AbsenceSystemRes deleteAbsence(AbsenceSystemReq req) {

		UUID uuid = UUID.fromString(req.getUuid());
		Optional<AbsenceSystem> absenceOp = absenceSystemDao.findById(uuid);

		if (!absenceOp.isPresent()) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_EMPTY.getMessage());
		}

		absenceSystemDao.deleteById(absenceOp.get().getUuid());

		return new AbsenceSystemRes(AbsenceSystemRtnCode.Delete_SUCCESSFUL.getMessage());
	}

	// ���u��ܦۤv������
	@Override
	public AbsenceSystemResList getAbsenceByEmployeeCode(HttpSession httpSession) {

		Object attValue = httpSession.getAttribute("employee_code");

		if (attValue != null) {
			String employeeCode = attValue.toString();
			List<AbsenceSystem> absenceList = absenceSystemDao.findByEmployeeCodeOrderByAbsenceDateDesc(employeeCode);
			AbsenceSystemResList res = new AbsenceSystemResList();
			res.setAbsenceSystemList(absenceList);
			return res;
		}
		return new AbsenceSystemResList(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
	}

	// ���u��J�~��M����밲��
	@Override
	public AbsenceSystemResList getAbsenceByEmployeeCodeAndDate(AbsenceSystemReq req, HttpSession httpSession) {

		if (req.getYear() == null || req.getMonth() == null) {
			return new AbsenceSystemResList(AbsenceSystemRtnCode.DATE_EMPTY.getMessage());
		}
		Object attValue = httpSession.getAttribute("employee_code");

		if (attValue != null) {
			String employeeCode = attValue.toString();
			List<AbsenceSystem> absenceList = absenceSystemDao.findByEmployeeCodeOrderByAbsenceDateDesc(employeeCode);
			List<AbsenceSystem> chosenList = new ArrayList<>();

			for (AbsenceSystem item : absenceList) {
				int itemYear = item.getAbsenceDate().getYear();
				int itemMonth = item.getAbsenceDate().getMonthValue();

				if (req.getYear() == itemYear && req.getMonth() == itemMonth) {
					chosenList.add(item);
				}
			}
			if (CollectionUtils.isEmpty(chosenList)) {
				return new AbsenceSystemResList(AbsenceSystemRtnCode.DATE_OF_ABSENCE_EMPTY.getMessage());
			}

			AbsenceSystemResList res = new AbsenceSystemResList();
			res.setAbsenceSystemList(chosenList);
			return res;
		}
		return new AbsenceSystemResList(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
	}
	

	// �̷ӥD�޵��ũM���ݳ�����ܰ���
	 @Override
	 public AbsenceSystemResList getAbsenceBySectionAndLevel(HttpSession httpSession) {

	  Object attValue = httpSession.getAttribute("employee_code");

	  if (!StringUtils.hasText(attValue.toString())) {
	   return new AbsenceSystemResList(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
	  }
	  // �尲�檺�H�����
	  EmployeeInfo manager = employeeInfoDao.findById(attValue.toString()).get();

	  // �Ҧ����u���
	  List<EmployeeInfo> employeeList = employeeInfoDao.findAll();

	  // �Ҧ�����
	  List<AbsenceSystem> absenceList = absenceSystemDao.findAllByOrderByAbsenceDateDesc();

	  // �n��ܪ�����
	  List<AbsenceSystemRes> absenceResList = new ArrayList<>();

	  for (AbsenceSystem absenceItem : absenceList) {

	   for (EmployeeInfo employeeItem : employeeList) {

	    if (manager.getSection().equals("�H��") && manager.getLevel() >= 1 && absenceItem.getYesOrNo() == 0) {

	     AbsenceSystemRes res = new AbsenceSystemRes();
	     res.setUuid(absenceItem.getUuid());
	     res.setEmployeeCode(employeeItem.getEmployeeCode());
	     res.setName(employeeItem.getName());
	     res.setSection(employeeItem.getSection());
	     res.setAbsenceReason(absenceItem.getAbsenceReason());
	     res.setAbsenceDate(absenceItem.getAbsenceDate());
	     absenceResList.add(res);
	    } else if (absenceItem.getEmployeeCode().equalsIgnoreCase(employeeItem.getEmployeeCode())) {

	     if (manager.getSection().equalsIgnoreCase(employeeItem.getSection())
	       && manager.getLevel() > employeeItem.getLevel() && absenceItem.getYesOrNo() == 0) {

	      AbsenceSystemRes res = new AbsenceSystemRes();
	      res.setUuid(absenceItem.getUuid());
	      res.setEmployeeCode(employeeItem.getEmployeeCode());
	      res.setName(employeeItem.getName());
	      res.setSection(employeeItem.getSection());
	      res.setAbsenceReason(absenceItem.getAbsenceReason());
	      res.setAbsenceDate(absenceItem.getAbsenceDate());
	      absenceResList.add(res);
	     }
	    }
	   }
	  }
	  AbsenceSystemResList finalRes = new AbsenceSystemResList();
	  finalRes.setAbsenceSystemResList(absenceResList);
	  return finalRes;
	 }

	// �D�ާ�ܰ���,�ñH�eemail�����u
	@Override
	public AbsenceSystemRes checkYesOrNo(AbsenceSystemReq req) {

		UUID uuid = UUID.fromString(req.getUuid());
		Optional<AbsenceSystem> absenceOp = absenceSystemDao.findById(uuid);

		AbsenceSystem absence = absenceOp.get();

		if (!absenceOp.isPresent()) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_EMPTY.getMessage());
		}

		if (req.getYesOrNo() == 1 && absenceOp.isPresent()) {

			WorkSystem absenceEmployee = new WorkSystem();
			absenceEmployee.setUuid(UUID.randomUUID());
			absenceEmployee.setEmployeeCode(absenceOp.get().getEmployeeCode());
			absenceEmployee.setAttendanceStatus("�а�");
			LocalDateTime localDateTime1 = absenceOp.get().getAbsenceDate().atStartOfDay();
			absenceEmployee.setWorkTime(localDateTime1);
			absenceEmployee.setOffWorkTime(localDateTime1);
			absenceEmployee.setAttendanceHours(0);

			workSystemDao.save(absenceEmployee);

			absenceOp.get().setYesOrNo(1);
			absenceSystemDao.save(absenceOp.get());

			EmployeeInfo employee = employeeInfoDao.findById(absenceOp.get().getEmployeeCode()).get();

			SimpleMailMessage gmail = new SimpleMailMessage();

			gmail.setFrom("kennymax22581997@gmail.com");
			gmail.setTo(employee.getEmployeeEmail());
			gmail.setSubject("�D���G�Z�H��ȹC�ΰ���^��");
			gmail.setText("�z�b " + absence.getAbsenceDate() + " ���𰲤w���");
			mailSender.send(gmail);

			AbsenceSystemRes res = new AbsenceSystemRes();
			res.setYesOrNo(true);
			res.setMessage(AbsenceSystemRtnCode.ABSENCE_ACCEPT.getMessage());
			return res;
		}
		EmployeeInfo employee = employeeInfoDao.findById(absence.getEmployeeCode()).get();

		absence.setYesOrNo(2);
		absenceSystemDao.save(absence);

		SimpleMailMessage gmail = new SimpleMailMessage();

		gmail.setFrom("kennymax22581997@gmail.com");
		gmail.setTo(employee.getEmployeeEmail());
		gmail.setSubject("�D���G�Z�H��ȹC�ΰ���^��");
		gmail.setText("�z�b " + absence.getAbsenceDate() + " ���𰲾D��ڵ�");
		mailSender.send(gmail);
		return new AbsenceSystemRes(AbsenceSystemRtnCode.ABSENCE_REJECT.getMessage());
	}

	// �P�_���u����(�p��2�����I)
	@Override
	public boolean checkEmployeeLevel(HttpSession httpSession) {

		Object attValue = httpSession.getAttribute("employee_code");

		if (attValue != null) {
			String employeeCode = attValue.toString();
			EmployeeInfo employeeInfo = employeeInfoDao.findById(employeeCode).get();
			if (employeeInfo.getLevel() > 1) {
				return true;
			}
			return false;
		}

		return false;
	}

	// ��s�а�����
	@Override
	public AbsenceSystemRes updateAbsence(AbsenceSystemReq req) {
		if (!StringUtils.hasText(req.getUuid())) {
			return new AbsenceSystemRes(AbsenceSystemRtnCode.UUID_EMPTY.getMessage());
		}
		UUID uuid = UUID.fromString(req.getUuid());
		AbsenceSystem absence = absenceSystemDao.findById(uuid).get();

		if (StringUtils.hasText(req.getAbsenceReason())) {
			absence.setAbsenceReason(req.getAbsenceReason());
			;
		}
		if (req.getAbsenceDate() != null) {
			absence.setAbsenceDate(req.getAbsenceDate());
			;
		}
		absenceSystemDao.save(absence);

		return new AbsenceSystemRes(absence, AbsenceSystemRtnCode.SUCCESSFUL.getMessage());
	}

	// ���u��ܦۤv�����D�ު�email
	 @Override
	 public EmployeeInfoRes getManagerEmailByEmployeeCode(HttpSession httpSession) {

	  Object attValue = httpSession.getAttribute("employee_code");

	  if (attValue == null) {
	   return new EmployeeInfoRes(AbsenceSystemRtnCode.EMPLOYEE_CODE_REQOIRED.getMessage());
	  }
	  List<EmployeeInfo> employeeInfoList = employeeInfoDao.findAll();
	  String employeeCode = attValue.toString();

	  EmployeeInfo employee = employeeInfoDao.findById(employeeCode).get();

	  EmployeeInfo employeeInfo = new EmployeeInfo();
	  List<EmployeeInfo> managerInfoList = new ArrayList<>();

	  for (EmployeeInfo item : employeeInfoList) {
	   if (item.getEmployeeCode().equalsIgnoreCase(employeeCode)) {
	    employeeInfo.setSection(item.getSection());
	    employeeInfo.setLevel(item.getLevel());
	   }
	  }
	  for (EmployeeInfo item : employeeInfoList) {
	   if (item.getSection().equalsIgnoreCase(employeeInfo.getSection())
	     && item.getLevel() > employeeInfo.getLevel()) {

	    managerInfoList.add(item);
	   }
	  }
	  if (!employee.getSection().equals("�H��")) {

	   for (EmployeeInfo item : employeeInfoList) {
	    if (item.getSection().equalsIgnoreCase("�H��") && item.getLevel() >= 1) {

	     managerInfoList.add(item);
	    }
	   }
	  } else if (employee.getSection().equals("�H��") && employee.getLevel() >= 2) {

	   managerInfoList.add(employee);
	  }
	  EmployeeInfoRes res = new EmployeeInfoRes();
	  res.setEmployeeInfoList(managerInfoList);
	  return res;
	 }

}
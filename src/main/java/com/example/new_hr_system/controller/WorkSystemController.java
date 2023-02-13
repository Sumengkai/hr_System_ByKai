package com.example.new_hr_system.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.new_hr_system.entity.WorkSystem;
import com.example.new_hr_system.service.ifs.WorkSystemService;
import com.example.new_hr_system.vo.WorkSystemReq;
import com.example.new_hr_system.vo.WorkSystemRes;

//@CrossOrigin 
@RestController
public class WorkSystemController {

	@Autowired
	private WorkSystemService workSystemService;
	// -------------------------------------------------

	// ---�n�X�ɧR���j�w���u�b�� (�i�H�R��)
	@PostMapping(value = "/api/httpSessionEmployeeCodeOut")
	public WorkSystemRes employeeCodeLoginOut(HttpSession httpSession) {
		httpSession.removeAttribute("EmployeeCode");
		return new WorkSystemRes("�n�X���\");
	}

	// ---�j�w�n�J���u�b��(�i�H�R��)
	@PostMapping(value = "/api/employeeCodeLogin") // �n�J�j�w�K�X<�����޿�> �A�h�����ط|�T�{�O�_���ӭ��u
	public WorkSystemRes employeeCodeLogin(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		httpSession.setAttribute("EmployeeCode", req.getEmployeeCode());
		return new WorkSystemRes("�n�J���\");

	}

//==========================================================================	

	/*--------------------(���u)���d�W�Z*/
	@PostMapping(value = "/api/punchToWork")
	public WorkSystemRes punchToWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s�����d");
		}
		return workSystemService.punchToWork(req);
	}

	/*--------------------(���u)���d�U�Z*/
	@PostMapping(value = "/api/punchToOffWork")
	public WorkSystemRes punchToOffWork(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s�����d�U�Z");
		}
		return workSystemService.punchToOffWork(req);
	}

	/*--------------------(���u)�j�M���d���*/
	@PostMapping(value = "/api/searchWorkInfoForStaff")
	public WorkSystemRes searchWorkInfoForStaff(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("�п�J�ۤv�����u�s���j�M�Ϊ̹��խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s��");
		}
		return workSystemService.searchWorkInfoForStaff(req);
	}

	/*--------------------(�D��)�j�M���d���*/ //1-2
	@PostMapping(value = "/api/searchWorkInfoForManager")
	public WorkSystemRes searchWorkInfoForManager(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s��");
		}
		return workSystemService.searchWorkInfoForManager(req);
	}

	/*--------------------(�j����)�R���ɶ��϶������d���*/  //2
	@PostMapping(value = "/api/deleteWorkInfoByDateBetween")
	public WorkSystemRes deleteWorkInfoByDateBetween(@RequestBody WorkSystemReq req) {
		if (!req.getPassword().equals("aaa")) {
			return new WorkSystemRes("�K�X���~");
		}
		return workSystemService.deleteWorkInfoByDateBetween(req);
	}

	/*--------------------(�D��)�s�W�m¾�欰*///1-2
	@PostMapping(value = "/api/creatAbsenteeismForManager")
	public WorkSystemRes creatAbsenteeismForManager(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s��");
		}
		return workSystemService.creatAbsenteeismForManager(req);
	}

	/*--------------------(�D��)�R���m¾�欰*///1-2
	@PostMapping(value = "/api/deleteAbsenteeismForManager")
	public WorkSystemRes deleteAbsenteeismForManager(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s��");
		}
		return workSystemService.deleteAbsenteeismForManager(req);
	}

	/*--------------------(���u)�L�X���d��T���U�Z���d��*/
	@PostMapping(value = "/api/getWorkInfoListToday")
	public WorkSystemRes getWorkInfoListToday(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s��");
		}
		return workSystemService.getWorkInfoListToday(req);
	}

	/*--------------------(�D��)�L�X���d��T���R���m¾�欰��*///1-2
	@PostMapping(value = "/api/getWorkInfoListAbsenteeism")
	public WorkSystemRes getWorkInfoListAbsenteeism(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s��");
		}
		return workSystemService.getWorkInfoListAbsenteeism(req);
	}

	/*--------------------(�D��)���u�S���d�U�Z��D�޸ɥ��U�Z�d*///2
	@PostMapping(value = "/api/updeateWorkOffTimeForManager")
	public WorkSystemRes updeateWorkOffTimeForManager(@RequestBody WorkSystemReq req) {
		return workSystemService.updeateWorkOffTimeForManager(req);
	}

	/*--------------------(�D��)���u�S���d�U�Z��D�޸ɥ��d*///2
	@PostMapping(value = "/api/forgotToPunchCard")
	public WorkSystemRes forgotToPunchCard(@RequestBody WorkSystemReq req, HttpSession httpSession) {
		Object employeeCode = httpSession.getAttribute("employee_code");
		if (employeeCode == null) {
			return new WorkSystemRes("���խ��s�n�J");
		}
		String employeeCodeString = httpSession.getAttribute("employee_code").toString();
		if (!employeeCodeString.equals(req.getManagerEmployeeCode())) {
			return new WorkSystemRes("�п�J�ۤv�����u�s��");
		}
		return workSystemService.forgotToPunchCard(req);
	}

}
/*
 * CustomerController.java
 * 
 * Copyright (C) 2018 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.BrotherhoodService;
import services.ProcessionService;
import services.RequestService;
import domain.Brotherhood;
import domain.Procession;
import domain.Request;
import domain.Status;

@Controller
@RequestMapping("/request/brotherhood/")
public class RequestBrotherhoodController extends AbstractController {

	@Autowired
	private RequestService		requestService;
	@Autowired
	private BrotherhoodService	brotherhoodService;
	@Autowired
	private ProcessionService	processionService;


	// Constructors -----------------------------------------------------------

	public RequestBrotherhoodController() {
		super();
	}

	// List ---------------------------------------------------------------		

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView requestsList() {
		ModelAndView result;

		Brotherhood loggedBrotherhood = this.brotherhoodService.securityAndBrotherhood();
		Collection<Request> requests = this.requestService.getRequestsByBrotherhood(loggedBrotherhood);

		result = new ModelAndView("brotherhood/requests");

		result.addObject("requests", requests);
		result.addObject("requestURI", "request/brotherhood/list.do");

		return result;
	}

	@RequestMapping(value = "/filter", method = RequestMethod.POST, params = "refresh")
	public ModelAndView requestsFilter(@Valid String fselect) {
		ModelAndView result;

		if (fselect.equals("ALL"))
			result = new ModelAndView("redirect:list.do");
		else {

			Status status = Status.APPROVED;
			if (fselect.equals("PENDING"))
				status = Status.PENDING;
			else if (fselect.equals("REJECTED"))
				status = Status.REJECTED;

			Brotherhood loggedBrotherhood = this.brotherhoodService.securityAndBrotherhood();
			Collection<Request> requests = this.requestService.getRequestsByBrotherhoodAndStatus(loggedBrotherhood, status);

			result = new ModelAndView("brotherhood/requests");

			result.addObject("requests", requests);
			result.addObject("requestURI", "request/brotherhood/filter.do");
		}

		return result;
	}

	@RequestMapping(value = "/filterProcession", method = RequestMethod.POST, params = "refresh2")
	public ModelAndView requestsFilterProcession(@Valid String fselect, @RequestParam int processionId) {
		ModelAndView result;
		Procession procession = new Procession();
		procession = this.processionService.findOne(processionId);

		Assert.isTrue(this.brotherhoodService.loggedBrotherhood().getProcessions().contains(procession));

		if (fselect.equals("ALL"))
			result = new ModelAndView("redirect:listProcession.do");
		else {

			Status status = Status.APPROVED;
			if (fselect.equals("PENDING"))
				status = Status.PENDING;
			else if (fselect.equals("REJECTED"))
				status = Status.REJECTED;

			List<Request> requests = this.requestService.getRequestsByProcessionAndStatus(procession, status);

			result = new ModelAndView("procession/brotherhood/requests");

			result.addObject("requests", requests);
			result.addObject("processionId", processionId);
			result.addObject("requestURI", "request/brotherhood/filterProcession.do");
		}

		return result;
	}
}


package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.BrotherhoodService;
import services.EnrolmentService;
import services.MemberService;
import domain.Brotherhood;
import domain.Enrolment;
import domain.Member;
import domain.StatusEnrolment;

@Controller
@RequestMapping("/enrolment/member")
public class EnrolmentController extends AbstractController {

	@Autowired
	private EnrolmentService	enrolmentService;

	@Autowired
	private MemberService		memberService;

	@Autowired
	private BrotherhoodService	brotherhoodService;


	public EnrolmentController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		List<Enrolment> enrolments = new ArrayList<Enrolment>();
		enrolments = this.enrolmentService.getEnrolmentsPerMember(this.memberService.loggedMember());

		result = new ModelAndView("enrolment/member/list");

		result.addObject("enrolments", enrolments);
		result.addObject("requestURI", "enrolment/member/list.do");
		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam int brotherhoodId) {
		Member m = new Member();
		m = this.memberService.loggedMember();
		Brotherhood brotherhood = new Brotherhood();
		brotherhood = this.brotherhoodService.findOne(brotherhoodId);

		Assert.notNull(m);
		ModelAndView result;
		this.memberService.loggedAsMember();
		Enrolment enrolment = new Enrolment();

		enrolment = this.enrolmentService.create();
		enrolment.setBrotherhood(brotherhood);
		enrolment.setMember(m);
		enrolment.setStatusEnrolment(StatusEnrolment.PENDING);

		result = this.createEditModelAndView(enrolment);
		result.addObject("brotherhoodId", brotherhoodId);
		return result;
	}
	@RequestMapping(value = "/dropout", method = RequestMethod.POST, params = "save")
	public ModelAndView dropOut(@RequestParam int enrolmentId) {
		ModelAndView result;
		Enrolment enrolment;

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1);

		enrolment = this.enrolmentService.findOne(enrolmentId);
		Assert.notNull(enrolment);

		if (enrolment.getStatusEnrolment() != StatusEnrolment.DROPOUT && enrolment.getDropOutDate() == null && this.memberService.loggedMember().getEnrolments().contains(enrolment)) {
			enrolment.setStatusEnrolment(StatusEnrolment.DROPOUT);
			enrolment.setDropOutDate(thisMoment);
			this.enrolmentService.save(enrolment);
			result = new ModelAndView("redirect:list.do");
		} else
			result = new ModelAndView("redirect:list.do");

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid Enrolment enrolment, BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(enrolment);
		else
			try {
				this.enrolmentService.createEnrolment(enrolment);
				result = new ModelAndView("redirect:list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndView(enrolment, "enrolment.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndView(Enrolment enrolment) {
		ModelAndView result;

		result = this.createEditModelAndView(enrolment, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Enrolment enrolment, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("enrolment/member/create");

		result.addObject("enrolment", enrolment);
		result.addObject("message", messageCode);

		return result;
	}

}

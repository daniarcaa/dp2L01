
package controllers;

import java.util.ArrayList;
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
import services.FloatService;
import services.ProcessionService;
import domain.Brotherhood;
import domain.Float;

@Controller
@RequestMapping("/float/brotherhood")
public class BrotherhoodController extends AbstractController {

	@Autowired
	private BrotherhoodService	brotherhoodService;

	@Autowired
	private FloatService		floatService;
	@Autowired
	private ProcessionService	processionService;


	//LIST
	//Lista de todos los floatt de esa brotherhood
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		this.brotherhoodService.loggedAsBrotherhood();
		Brotherhood loggedBrotherhood = this.brotherhoodService.loggedBrotherhood();

		Boolean hasArea = !(loggedBrotherhood.getArea() == null);

		List<Float> allFloats = new ArrayList<Float>();

		allFloats = this.floatService.showBrotherhoodFloats();

		result = new ModelAndView("float/brotherhood/list");

		result.addObject("allFloats", allFloats);
		result.addObject("requestURI", "float/brotherhood/list.do");
		result.addObject("hasArea", hasArea);
		return result;
	}

	//LIST PICTURES
	@RequestMapping(value = "/picture/list", method = RequestMethod.GET)
	public ModelAndView listBro(@RequestParam int floatId, @RequestParam boolean procession) {

		ModelAndView result;

		List<String> pictures;

		domain.Float floatt;

		floatt = this.floatService.findOne(floatId);

		Assert.notNull(floatt);

		pictures = floatt.getPictures();

		result = new ModelAndView("picture/brotherhood/picturesFloat");

		result.addObject("pictures", pictures);
		result.addObject("requestURI", "float/brotherhood/picture/list.do");
		result.addObject("procession", procession);

		return result;
	}

	//CREATE
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		Brotherhood bro = new Brotherhood();
		bro = this.brotherhoodService.loggedBrotherhood();

		Assert.isTrue(bro.getArea() != null);
		ModelAndView result;
		this.brotherhoodService.loggedAsBrotherhood();
		Float floatt = new Float();

		floatt = this.floatService.create();

		result = this.createEditModelAndView(floatt);
		return result;
	}

	//EDIT
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam int floatId) {
		ModelAndView result;

		this.brotherhoodService.loggedAsBrotherhood();
		Brotherhood loggedBrotherhood = this.brotherhoodService.loggedBrotherhood();

		domain.Float floatt = this.floatService.findOne(floatId);

		Assert.notNull(floatt);
		Assert.isTrue(loggedBrotherhood.getFloats().contains(floatt));

		result = this.createEditModelAndView(floatt);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveFloat(@Valid domain.Float floatt, BindingResult binding) {

		ModelAndView result;
		this.brotherhoodService.loggedAsBrotherhood();
		final Brotherhood loggedBrotherhood = this.brotherhoodService.loggedBrotherhood();
		Assert.isTrue(!(loggedBrotherhood.getArea().equals(null)));
		//floatt = this.floatService.reconstruct(floatt, binding);
		//floatt = this.floatService.reconstruct(floatt, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(floatt);
		else
			try {
				this.floatService.save(floatt);
				//TODO:
				result = new ModelAndView("redirect:/float/brotherhood/list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndView(floatt, "brotherhood.commit.error");

			}
		return result;
	}

	protected ModelAndView createEditModelAndView(Float floatt) {
		ModelAndView result;

		result = this.createEditModelAndView(floatt, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Float floatt, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("float/brotherhood/create");

		result.addObject("floatt", floatt);
		result.addObject("message", messageCode);

		return result;
	}

	//DELETE
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(domain.Float floatt, BindingResult binding) {
		this.brotherhoodService.loggedAsBrotherhood();
		ModelAndView result;
		Brotherhood brother = new Brotherhood();
		brother = this.brotherhoodService.loggedBrotherhood();
		List<Float> floatts = new ArrayList<Float>();

		floatts = this.brotherhoodService.getFloatsByBrotherhood(brother);

		if (!(floatts.contains(floatt)))
			return new ModelAndView("redirect:list.do");

		try {
			this.floatService.remove(floatt);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(floatt, "message.commit.error");

		}
		return result;
	}

}

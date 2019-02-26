
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.AdminService;
import services.ConfigurationService;
import services.MessageService;
import domain.Message;

@Controller
@RequestMapping("/broadcast/administrator")
public class AdministratorBroadcastMessage extends AbstractController {

	@Autowired
	private AdminService			adminService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private MessageService			messageService;

	@Autowired
	private ConfigurationService	configurationService;


	public AdministratorBroadcastMessage() {

		super();
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Message message;

		message = this.messageService.create();
		result = new ModelAndView("broadcast/administrator/send");
		result.addObject("messageSend", message);
		result.addObject("priority", this.configurationService.getConfiguration().getPriorityLvl());

		return result;
	}
	//Save
	@RequestMapping(value = "/send", method = RequestMethod.POST, params = "send")
	public ModelAndView send(@ModelAttribute("messageSend") Message message, BindingResult binding) {
		ModelAndView result;

		message = this.adminService.reconstruct(message, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(message);
		else
			try {
				this.adminService.broadcastMessage(message);
				result = new ModelAndView("redirect:send.do");

			} catch (Throwable oops) {
				result = this.createEditModelAndView(message, "message.commit.error");
			}
		return result;
	}
	protected ModelAndView createEditModelAndView(Message message) {
		ModelAndView result;

		result = this.createEditModelAndView(message, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Message message, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("broadcast/administrator/send");
		result.addObject("messageSend", message);
		result.addObject("priority", this.configurationService.getConfiguration().getPriorityLvl());
		result.addObject("message", messageCode);

		return result;
	}

}

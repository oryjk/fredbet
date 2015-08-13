package de.fred4jupiter.fredbet.web.matches;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.SecurityBean;

@Controller
@RequestMapping("/matches")
public class MatchController {

	@Autowired
	private MatchService matchService;
	
	@Autowired
	private SecurityBean securityBean;
	
	@Autowired
	private MessageUtil messageUtil;

	@RequestMapping
	public ModelAndView list() {
		List<MatchCommand> matches = matchService.findAllMatches(securityBean.getCurrentUserName());
		return new ModelAndView("matches/list", "allMatches", matches);
	}

	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") String matchId) {
		MatchCommand matchCommand = matchService.findByMatchId(matchId);
		return new ModelAndView("matches/form", "matchCommand", matchCommand);
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createForm(@ModelAttribute MatchCommand matchCommand) {
		matchCommand.setKickOffDate(new Date());
		return "matches/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid MatchCommand matchCommand, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("matches/form", "formErrors", result.getAllErrors());
		}

		matchService.save(matchCommand);

		String msg = "Spiel " + matchCommand.getTeamNameOne() + " gegen " + matchCommand.getTeamNameTwo() + " angelegt/aktualisiert!";
		messageUtil.addInfoMsg(redirect, msg);
		return new ModelAndView("redirect:/matches");
	}
}
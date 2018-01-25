package com.vcredit.o2o.web;

import com.vcredit.o2o.enumtype.Constants;
import com.vcredit.o2o.model.Qcr;
import com.vcredit.o2o.service.QcrService;
import com.vcredit.o2o.service.RelQcruserService;
import com.vcredit.o2o.vo.JsonResult;
import com.vcredit.o2o.vo.WeixinConcern;
import com.vcredit.o2o.vo.WeixinUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class WeixinController extends BaseController{
	@Autowired
	private QcrService qcrService;
	@Autowired
	private RelQcruserService relQcruserService;


	@ResponseBody
	@RequestMapping("/weixin/upload")
	public JsonResult upLoad(@Valid @RequestBody List<WeixinUpload> uploads){
		for(WeixinUpload upload:uploads){
			qcrService.insert(new Qcr(upload.getUrl(),upload.getSceneId(),Constants.StatusEnum.NO.getCode(),new Date(),Constants.qcrType.LONG.getCode()));
		}
		return new JsonResult();
	}

	@ResponseBody
	@RequestMapping("/weixin/concern")
	public JsonResult concern(@Valid @RequestBody WeixinConcern concern,BindingResult result) throws Exception{
		if(Constants.weixinConcernStatus.CONCERN.getCode().equals(concern.getStatus()))
			relQcruserService.concern(concern.getOpenId(),concern.getSceneId(),concern.getThirdName());
		else if(Constants.weixinConcernStatus.CANCEL_CONCERN.getCode().equals(concern.getStatus()))
			relQcruserService.cancelConcern(concern.getOpenId());
		return new JsonResult();
	}

	@ResponseBody
	@RequestMapping("/weixin/users")
	public JsonResult users(@Valid @RequestBody List<String> baseVos) throws Exception{
		for(String openId:baseVos){
			System.err.println(openId);
			relQcruserService.concern(openId, "", "");
		}
		return new JsonResult();
	}




	@ResponseBody
	@RequestMapping(value = "/userLeave",method = RequestMethod.POST)
	public JsonResult userLeave(@RequestParam("agentId")String agentId){
		JsonResult jsonResult = new JsonResult();
		try{
			qcrService.userLeave(Long.parseLong(agentId));
			jsonResult.setResult(0L);
			jsonResult.setContent("离职成功");
			return jsonResult;
		}catch (Exception e){
			jsonResult.setResult(1L);
			jsonResult.setContent("离职失败");
			return jsonResult;
		}

	}



}

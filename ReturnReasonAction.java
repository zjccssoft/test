package com.regaltec.ida30.svr.basedata.open.action;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.regaltec.common.util.GuidGenerator;
import com.regaltec.common.util.StringHelper;
import com.regaltec.framework.base.factory.XmlFactory;
import com.regaltec.framework.org.user.vo.UserVO;
import com.regaltec.ida30.svr.basedata.open.bo.IReturnReasonBO;
import com.regaltec.ida30.svr.basedata.open.bo.IReturnReasonCommonBO;
import com.regaltec.ida30.svr.basedata.open.bo.ReturnReasonUtil;
import com.regaltec.ida30.svr.basedata.open.vo.ReturnReasonCommonVO;
import com.regaltec.ida30.svr.basedata.open.vo.ReturnReasonVO;
import com.regaltec.ida30.svr.process.helper.DealHelper;

/**
 * <p>标题:异常原因ACTION</p> 
 * <p>描述:异常原因ACTION</p> 
 * <p>版权: Copyright (c) 2013 </p>
 * <p>公司: 中通服软件科技有限公司</p>
 * @version 1.0
 * @author 叶健强
 */
public class ReturnReasonAction extends DispatchAction
{
   /**
    * 加载常用异常原因
    * @param 
    * @return
    * @throws Exception
    */
   public void getReturnReasonById(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         String returnReasonConfigId = request.getParameter("returnReasonConfigId");
         IReturnReasonBO returnReasonBO = (IReturnReasonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonBO");
         ReturnReasonVO vo = returnReasonBO.find(Integer.parseInt(returnReasonConfigId));
         if (vo == null)
         {
            jo.put("state", "0");
         }
         else
         {
            jo.put("state", "1");
            jo.put("returnReasonConfigId", vo.getReturnReasonConfigId()+"");
            jo.put("returnReasonId", vo.getReturnReasonId() + "");
            jo.put("returnReasonName", vo.getReturnReasonName());
            jo.put("subReturnReasonId", vo.getSubReturnReasonId()+"");
            jo.put("subReturnReasonName", vo.getSubReturnReasonName());
            jo.put("sceneDesc", vo.getSceneDesc());
            jo.put("reasonType", vo.getReasonType());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
   
   /**
    * 加载异常原因
    * @param 
    * @return
    * @throws Exception
    */
   public void loadReturnReasonIds(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         String nativeNetId = StringHelper.convertStringNull(request.getParameter("nativeNetId"));
         String specialtyId = StringHelper.convertStringNull(request.getParameter("specialtyId"));
         String billType = StringHelper.convertStringNull(request.getParameter("billType"));
         String mainSn = StringHelper.convertStringNull(request.getParameter("mainSn"));
         String[] mainSns = StringHelper.parserString(mainSn, "$");
         IReturnReasonBO returnReasonBO = (IReturnReasonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonBO");
         Collection<ReturnReasonVO> collection = null;
         if("CP".equals(billType))
         {
            collection = returnReasonBO.qryCPByNativeNetId(nativeNetId, specialtyId);  
         }
         else 
         {
            if (mainSns.length == 1)
            {
               collection = returnReasonBO.qrySingleByNativeNetId(nativeNetId, specialtyId);       
            }
            else 
            {
               collection = returnReasonBO.qryByNativeNetId(nativeNetId , specialtyId);       
            }   
         }
         if (collection == null || collection.size() == 0)
         {
            jo.put("state", "0");
         }
         else
         {
            jo.put("state", "1");
            MessageFormat format = new MessageFormat(ReturnReasonUtil.PATTERN_OPTION);
            StringBuilder returnReasonIdOption = new StringBuilder();
            StringBuilder returnReasonConfigIdOption = new StringBuilder();
            Iterator<ReturnReasonVO> iterator = collection.iterator();
            int returnReasonId = 0;
            while (iterator.hasNext())
            {
               ReturnReasonVO vo = (ReturnReasonVO) iterator.next();
               if (returnReasonId != vo.getReturnReasonId())
               {
                  returnReasonIdOption.append(format.format(new Object[]{vo.getReturnReasonId()+"", vo.getReturnReasonName()}));
                  returnReasonId = vo.getReturnReasonId();
               }
               String optionValue = "";
               if (vo.getSubReturnReasonName() == null || vo.getSubReturnReasonName().length() == 0)
               {
                  optionValue = vo.getReturnReasonName();
               }
               else 
               {
                  optionValue = vo.getSubReturnReasonName();
               }
               returnReasonConfigIdOption.append(format.format(new Object[]{vo.getReturnReasonConfigId()+"", optionValue}));
            }
            jo.put("returnReasonIdOption", returnReasonIdOption.toString());
            jo.put("returnReasonConfigIdOption", returnReasonConfigIdOption.toString());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
   
   /**
    * 加载异常原因小类
    * @param 
    * @return
    * @throws Exception
    */
   public void loadSubReturnReasonIds(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         UserVO currUserVO = (UserVO) request.getSession().getAttribute("CurrentUser");
         int returnReasonId = Integer.parseInt(request.getParameter("returnReasonId"));
         String specialtyId = StringHelper.convertStringNull(request.getParameter("specialtyId"));
         String billType = StringHelper.convertStringNull(request.getParameter("billType"));
         String mainSn = StringHelper.convertStringNull(request.getParameter("mainSn"));
         String[] mainSns = StringHelper.parserString(mainSn, "$");
         IReturnReasonBO returnReasonBO = (IReturnReasonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonBO");
         Collection<ReturnReasonVO> collection = null;
         if("CP".equals(billType))
         {
            collection = returnReasonBO.qryCPSubByNativeNetId(returnReasonId, currUserVO.getNativeNetId(), specialtyId);
         }
         else 
         {
            if (mainSns.length == 1)
            {
               collection = returnReasonBO.qrySingleSubByNativeNetId(returnReasonId, currUserVO.getNativeNetId() , specialtyId);         
            }
            else 
            {
               collection = returnReasonBO.qrySubByNativeNetId(returnReasonId, currUserVO.getNativeNetId() , specialtyId);    
            }        
         }
         if (collection == null || collection.size() == 0)
         {
            jo.put("state", "0");
         }
         else
         {
            jo.put("state", "1");
            MessageFormat format = new MessageFormat(ReturnReasonUtil.PATTERN_OPTION);
            String optionValue = "";
            StringBuilder returnReasonConfigIdOption = new StringBuilder();
            Iterator<ReturnReasonVO> iterator = collection.iterator();
            while (iterator.hasNext())
            {
               ReturnReasonVO vo = (ReturnReasonVO) iterator.next();
               if (vo.getSubReturnReasonName() == null || vo.getSubReturnReasonName().length() == 0)
               {
                  optionValue = vo.getReturnReasonName();
               }
               else 
               {
                  optionValue = vo.getSubReturnReasonName();
               }
               returnReasonConfigIdOption.append(format.format(new Object[]{vo.getReturnReasonConfigId()+"", optionValue})); 
            }
            jo.put("returnReasonConfigIdOption", returnReasonConfigIdOption.toString());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
   
   /**
    * 加载常用异常原因
    * @param 
    * @return
    * @throws Exception
    */
   public void loadCommonData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONArray array = new JSONArray();
      try
      {
         UserVO currUserVO = (UserVO) request.getSession().getAttribute("CurrentUser");
         IReturnReasonCommonBO reasonCommonBO = (IReturnReasonCommonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonCommonBO");
         Collection<ReturnReasonCommonVO> collection = reasonCommonBO.qryByNativeNetId(currUserVO.getNativeNetId());
         if (collection != null && collection.size() > 0)
         {
            Iterator<ReturnReasonCommonVO> iterator = collection.iterator();
            while (iterator.hasNext())
            {
               ReturnReasonCommonVO vo = (ReturnReasonCommonVO) iterator.next();
               JSONObject jo = new JSONObject();
               jo.put("commonId", vo.getCommonId());
               jo.put("returnReasonCommon", vo.getReturnReasonCommon());
               jo.put("returnReasonConfigId", vo.getReturnReasonConfigId());
               jo.put("nativeNetName", vo.getNativeNetName());
               jo.put("sortOrder", vo.getSortOrder());
               jo.put("returnReasonName", vo.getReturnReasonName());
               jo.put("subReturnReasonName", vo.getSubReturnReasonName());
               array.put(jo);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      PrintWriter outs = response.getWriter();
      outs.print(array.toString());
      outs.close();
   }
   
   /**
    * 新增常用异常原因
    * @param 
    * @return
    * @throws Exception
    */
   public void addReasonCommon(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         UserVO currUserVO = (UserVO) request.getSession().getAttribute("CurrentUser");
         
         String returnReasonCommon = request.getParameter("returnReasonCommon");
         String returnReasonConfigId = request.getParameter("returnReasonConfigId");
         
         ReturnReasonCommonVO vo = new ReturnReasonCommonVO();
         vo.setCommonId(GuidGenerator.genRandomGUID());
         vo.setReturnReasonCommon(returnReasonCommon);
         vo.setReturnReasonConfigId(Integer.parseInt(returnReasonConfigId));
         if(ReturnReasonUtil.NATIVENETID_SUPER.equals(currUserVO.getNativeNetId()))
         {
            vo.setNativeNetId(request.getParameter("nativeNetId"));
         }
         else 
         {
            vo.setNativeNetId(currUserVO.getNativeNetId());
         }
         vo.setIsValid(ReturnReasonUtil.RETURNREASON_ISVALID_Y);
         IReturnReasonCommonBO reasonCommonBO = (IReturnReasonCommonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonCommonBO");         
         vo.setSortOrder(reasonCommonBO.nextSortOrder(vo.getNativeNetId()));
         reasonCommonBO.add(vo);
         
         jo.put("state", "1");
         jo.put("msg", "操作成功！");         
      }
      catch (Exception e)
      {
         e.printStackTrace();
         jo.put("state", "0");
         jo.put("msg", "操作失败！提示：" + DealHelper.getNestedExceptionMsg(e));
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
   
   /**
    * 修改常用异常原因
    * @param 
    * @return
    * @throws Exception
    */
   public void modReasonCommon(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         String commonId = request.getParameter("commonId");
         String returnReasonCommon = request.getParameter("returnReasonCommon");
         String returnReasonConfigId = request.getParameter("returnReasonConfigId");

         IReturnReasonCommonBO reasonCommonBO = (IReturnReasonCommonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonCommonBO");     
         ReturnReasonCommonVO vo = reasonCommonBO.find(commonId);
         if (vo != null)
         {
            vo.setReturnReasonCommon(returnReasonCommon);
            vo.setReturnReasonConfigId(Integer.parseInt(returnReasonConfigId));
            reasonCommonBO.mod(vo);
            jo.put("state", "1");
            jo.put("msg", "操作成功！");         
         }
         else 
         {
            jo.put("state", "0");
            jo.put("msg", "操作失败！记录不存在！");   
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         jo.put("state", "0");
         jo.put("msg", "操作失败！提示：" + DealHelper.getNestedExceptionMsg(e));
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
   
   /**
    * 删除常用异常原因
    * @param 
    * @return
    * @throws Exception
    */
   public void delReasonCommon(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         String commonId = request.getParameter("commonId");
         IReturnReasonCommonBO reasonCommonBO = (IReturnReasonCommonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonCommonBO");     
         reasonCommonBO.del(commonId);
         jo.put("state", "1");
         jo.put("msg", "操作成功！"); 
      }
      catch (Exception e)
      {
         e.printStackTrace();
         jo.put("state", "0");
         jo.put("msg", "操作失败！提示：" + DealHelper.getNestedExceptionMsg(e));
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
   
   /**
    * 修改常用异常原因排序
    * @param 
    * @return
    * @throws Exception
    */
   public void modReasonCommonSortOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         String commonId = request.getParameter("commonId");
         String sortOrder = request.getParameter("sortOrder");
         String nextCommonId = request.getParameter("nextCommonId");
         String nextOrder = request.getParameter("nextOrder");
         IReturnReasonCommonBO reasonCommonBO = (IReturnReasonCommonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonCommonBO");     
         reasonCommonBO.swapSortOrder(commonId, Integer.parseInt(sortOrder), nextCommonId, Integer.parseInt(nextOrder));
         jo.put("state", "1");
         jo.put("msg", "修改排序成功！"); 
      }
      catch (Exception e)
      {
         e.printStackTrace();
         jo.put("state", "0");
         jo.put("msg", "操作失败！提示：" + DealHelper.getNestedExceptionMsg(e));
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
   /**
    * 加载带宽型异常原因
    * @param 
    * @return
    * @throws Exception
    */
   public void loadReturnReasonIdsBW(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         String nativeNetId = request.getParameter("nativeNetId");
         String specialtyId = request.getParameter("specialtyId");
         String billType = request.getParameter("billType");
         billType = java.net.URLDecoder.decode(billType, "UTF-8");
         IReturnReasonBO returnReasonBO = (IReturnReasonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonBO");
         Collection<ReturnReasonVO> collection = returnReasonBO.qryByNativeNetId(nativeNetId , specialtyId , billType);
         if (collection == null || collection.size() == 0)
         {
            jo.put("state", "0");
         }
         else
         {
            jo.put("state", "1");
            MessageFormat format = new MessageFormat(ReturnReasonUtil.PATTERN_OPTION);
            StringBuilder returnReasonIdOption = new StringBuilder();
            StringBuilder returnReasonConfigIdOption = new StringBuilder();
            Iterator<ReturnReasonVO> iterator = collection.iterator();
            int returnReasonId = 0;
            while (iterator.hasNext())
            {
               ReturnReasonVO vo = (ReturnReasonVO) iterator.next();
               if (returnReasonId != vo.getReturnReasonId())
               {
                  returnReasonIdOption.append(format.format(new Object[]{vo.getReturnReasonId()+"#"+ vo.getReturnReasonConfigId(), vo.getReturnReasonName()}));
                  returnReasonId = vo.getReturnReasonId();
               }
               returnReasonConfigIdOption.append(format.format(new Object[]{vo.getReturnReasonConfigId()+"", vo.getSubReturnReasonName()}));
            }
            jo.put("returnReasonIdOption", returnReasonIdOption.toString());
            jo.put("returnReasonConfigIdOption", returnReasonConfigIdOption.toString());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
   /**
    * 带宽型加载异常原因小类
    * @param 
    * @return
    * @throws Exception
    */
   public void loadSubReturnReasonIdsBW(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception
   {
      JSONObject jo = new JSONObject();
      try
      {
         UserVO currUserVO = (UserVO) request.getSession().getAttribute("CurrentUser");
         String returnReasonIdStr = request.getParameter("returnReasonId");
         int returnReasonId = 0;
         if(returnReasonIdStr.indexOf("#") > -1)
         {
            String[] backCaseArray = StringUtils.split(returnReasonIdStr , "#");
            returnReasonId = Integer.parseInt(backCaseArray[0]);
         }
         else
            returnReasonId = Integer.parseInt(returnReasonIdStr);
         String specialtyId = request.getParameter("specialtyId");
         String billType = request.getParameter("billType");
         billType = java.net.URLDecoder.decode(billType, "UTF-8");
         IReturnReasonBO returnReasonBO = (IReturnReasonBO) XmlFactory.getObject("IDA_OPEN_IReturnReasonBO");
         Collection<ReturnReasonVO> collection = returnReasonBO.qrySubByNativeNetId(returnReasonId, currUserVO.getNativeNetId() , specialtyId , billType);
         if (collection == null || collection.size() == 0)
         {
            jo.put("state", "0");
         }
         else
         {
            jo.put("state", "1");
            MessageFormat format = new MessageFormat(ReturnReasonUtil.PATTERN_OPTION);
            String optionValue = "";
            StringBuilder returnReasonConfigIdOption = new StringBuilder();
            Iterator<ReturnReasonVO> iterator = collection.iterator();
            while (iterator.hasNext())
            {
               ReturnReasonVO vo = (ReturnReasonVO) iterator.next();
               if (vo.getSubReturnReasonName() == null || vo.getSubReturnReasonName().length() == 0)
               {
                  optionValue = vo.getReturnReasonName();
               }
               else 
               {
                  optionValue = vo.getSubReturnReasonName();
               }
               returnReasonConfigIdOption.append(format.format(new Object[]{vo.getReturnReasonConfigId()+"", optionValue})); 
            }
            jo.put("returnReasonConfigIdOption", returnReasonConfigIdOption.toString());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      PrintWriter outs = response.getWriter();
      outs.print(jo);
      outs.close();
   }
}

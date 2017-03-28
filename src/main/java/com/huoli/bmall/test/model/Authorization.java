package com.huoli.bmall.test.model;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sunline.kylin.web.core.client.Flat;

/**
 * @author xzl
 * @category 按钮权限验证--覆盖
 * Created by xlizy on 2017/3/14.
 */
public class Authorization {

    public Authorization() {
    }

    public static boolean accredit(Class<?> c) {
        return check(c.getName());
    }

    public static boolean accredit(Object widget, ResourceUnit auth) {
        return check(auth.getPath());
    }

    public static boolean accredit(ResourceUnit auth) {
        return check(auth.getPath());
    }

    public static boolean accreditForButton(Object widget, ResourceUnit auth) {
        return check(auth.getPath());
    }
    /**
     * 可以考虑一个模块用一个map
     *
     */
    private static final Map<String,String> authMap;
    static{
    	authMap = new HashMap<String,String>(64);
    	//1.规则平台
    	authMap.put("com.sunline.kylin.rules.client.pages.strategy.StrategyPage[addBtnAuth]","oldErmas:gzptIndex:addBtnAuth");
    	authMap.put("com.sunline.kylin.rules.client.pages.strategy.StrategyPage[editBtnAuth]","oldErmas:gzptIndex:editBtnAuth");
    	authMap.put("com.sunline.kylin.rules.client.pages.strategy.StrategyPage[unDeployBtnAuth]","oldErmas:gzptIndex:unDeployBtnAuth");
    	authMap.put("com.sunline.kylin.rules.client.pages.strategy.StrategyPage[compileBtnAuth]","oldErmas:gzptIndex:compileBtnAuth");
    	authMap.put("com.sunline.kylin.rules.client.pages.strategy.StrategyPage[deployBtnAuth]","oldErmas:gzptIndex:deployBtnAuth");
    	authMap.put("com.sunline.kylin.rules.client.pages.strategy.StrategyPage[deleteBtnAuth]","oldErmas:gzptIndex:deleteBtnAuth");
    	//2.组织管理
    	//组织数据
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[tabOrgAuth]","oldErmas:org_list:orgAuth");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[tabPartmentAuth]","oldErmas:org_list:departmentAuth");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[tabPositionAuth]","oldErmas:org_list:positionAuth");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[tabStaffAuth]","oldErmas:org_list:userAuth");

    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[userAddBtnAuth]","oldErmas:org_list:userAdd");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[positionAddBtnAuth]","oldErmas:org_list:positionAdd");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[departmentAddBtnAuth]","oldErmas:org_list:departmentAdd");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[orgAddBtnAuth]","oldErmas:org_list:orgAdd");
    	
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[userEditBtnAuth]","oldErmas:org_list:userEdit");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[positionEditBtnAuth]","oldErmas:org_list:positionEdit");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[departmentEditBtnAuth]","oldErmas:org_list:departmentEdit");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[orgEditBtnAuth]","oldErmas:org_list:orgEdit");
    	
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[userDeleteBtnAuth]","oldErmas:org_list:userDelete");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[positionDeleteBtnAuth]","oldErmas:org_list:positionDelete");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[departmentDeleteBtnAuth]","oldErmas:org_list:departmentDelete");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[orgDeleteBtnAuth]","oldErmas:org_list:orgDelete");
    	
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[positionRelateRoleBtnAuth]","oldErmas:org_list:positionRelateRole");
    	authMap.put("com.sunline.kylin.web.org.client.pages.org.OrgPage[userRelateRoleBtnAuth]","oldErmas:org_list:userRelateRole");
    	//3.角色管理
    	authMap.put("com.sunline.kylin.web.auth.client.pages.role.RoleAddPage","oldErmas:role_list:roleAdd");
    	authMap.put("com.sunline.kylin.web.auth.client.pages.role.RoleEditPage","oldErmas:role_list:roleEdit");
    	authMap.put("com.sunline.kylin.web.auth.client.pages.role.RolePageForSSO[deleteBtnAuth]","oldErmas:role_list:roleDelete");
    	//4.字典管理
    	authMap.put("com.sunline.kylin.web.code.client.pages.code.CodePage[addBtnAuth]","oldErmas:code_list:codeAdd");
    	authMap.put("com.sunline.kylin.web.code.client.pages.code.CodePage[deleteBtnAuth]","oldErmas:code_list:codeDelete");
    	authMap.put("com.sunline.kylin.web.code.client.pages.code.CodePage[editBtnAuth]","oldErmas:code_list:codeEdit");
    	//5.字典类型管理
    	authMap.put("com.sunline.kylin.web.code.client.pages.codetype.CodeTypeAddPage","oldErmas:code_type_list:codeTypeAdd");
    	authMap.put("com.sunline.kylin.web.code.client.pages.codetype.CodeTypeEditPage","oldErmas:code_type_list:codeTypeEdit");
    	authMap.put("com.sunline.kylin.web.code.client.pages.codetype.CodeTypePage[deleteBtnAuth]","oldErmas:code_type_list:codeTypeDel");
    	//6.资源管理
    	authMap.put("com.sunline.kylin.web.auth.client.pages.resource.ResourceAddPage","oldErmas:resource_list:resourceAdd");
    	authMap.put("com.sunline.kylin.web.auth.client.pages.resource.ResourceEditPage","oldErmas:resource_list:resourceEdit");
    	authMap.put("com.sunline.kylin.web.auth.client.pages.resource.ResourcePage[deleteBtnAuth]","oldErmas:resource_list:resourceDelete");
    	authMap.put("com.sunline.kylin.web.auth.client.pages.resource.ResourcePage[setResIconBtnAuth]","oldErmas:resource_list:setResIcon");
    	//7.资源维护
    	authMap.put("com.sunline.kylin.web.auth.client.pages.resMaintenance.ResMaintenanceEditPage","oldErmas:res_maintenance:resEdit");
    	authMap.put("com.sunline.kylin.web.auth.client.pages.resMaintenance.ResMaintenancePage[deleteBtnAuth]","oldErmas:res_maintenance:resDelete");
    	authMap.put("com.sunline.kylin.web.auth.client.pages.resMaintenance.ResMaintenancePage[setResIconBtnAuth]","oldErmas:res_maintenance:setResIcon");
    }
    private static boolean check(final String old){
        Collection<String> ps = Flat.get().getContext().getCurrentUser().getPermissions();
        if(ps != null){
			final String ssoAuth = authMap.get(old);
//          Dialog.alertError("---新的:"+ps+",判断权限"+ps.contains(ssoAuth), "Error-xlizy");
			if( ssoAuth != null && ps.contains(ssoAuth)){//查看是否存在对应的新的按钮权限
				return true;
			}
		}
        return false;
    }
}

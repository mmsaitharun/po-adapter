package oneapp.workbox.poadapter.rest;
//package com.incture.pmc.rest;
//
//import javax.ejb.EJB;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//
//import com.incture.pmc.poadapter.services.DatabaseManagementFacadeLocal;
///**
// * Rest implementation for DatabaseManagementFacadeLocal interface
// * 
// * @author INC00400
// * @version 1.0
// * @since 2017-05-09
// */
//@Path("/events")
//public class DataBaseManagementFacadeRest {
//	
//	@EJB
//	private DatabaseManagementFacadeLocal dbLocal;
//	
//	@Path("/task/{taskInstanceId}")
//	@GET
//	@Consumes({MediaType.APPLICATION_JSON })
//	@Produces({MediaType.APPLICATION_JSON })
//	public void saveUpdateTaskEvents(@PathParam("taskInstanceId") String taskInstanceId){
//		 dbLocal.updateTaskEvents(taskInstanceId);
//	}
//}

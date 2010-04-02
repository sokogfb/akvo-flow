package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.dao.KMLDAO;
import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.device.domain.DeviceFiles;

@SuppressWarnings("serial")
public class WaterForPeopleMappingGoogleServlet extends HttpServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(WaterForPeopleMappingGoogleServlet.class.getName());

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	
		String showKML = req.getParameter("showKML");
		@SuppressWarnings("unused")
		String processFile = req.getParameter("processFile");
		String listFiles = req.getParameter("listFiles");
		String testVelocity = req.getParameter("testVelocity");
		String showRegion = req.getParameter("showRegion");

		if (showKML != null) {
			Long kmlKey = null;
			if (req.getParameter("kmlID") != null) {
				kmlKey = Long.parseLong(req.getParameter("kmlID"));
			}
			if (kmlKey != null) {
				KMLDAO kmlDAO = new KMLDAO();
				String kmlString = kmlDAO.getKML(kmlKey);				
				resp.setContentType("application/vnd.google-earth.kml+xml");
				resp.getWriter().println(kmlString);
			} else {
				KMLGenerator kmlGen = new KMLGenerator();
				String placemarksDocument = kmlGen
						.generateDocument("PlacemarkTabs.vm");
				resp.setContentType("application/vnd.google-earth.kml+xml");
				resp.getWriter().println(placemarksDocument);
			}
		} else if (showRegion != null) {
			KMLGenerator kmlGen = new KMLGenerator();
			String placemarksDocument = kmlGen
					.generateRegionDocumentString("Regions.vm");
			resp.setContentType("application/vnd.google-earth.kml+xml");
			resp.getWriter().println(placemarksDocument);
//		} else if (listFiles != null) {
//			//TODO move this to a DAO
//			// List processed files
//			javax.jdo.Query query = pm.newQuery("select from  "
//					+ DeviceFiles.class.getName());
//			List<DeviceFiles> entries = (List<DeviceFiles>) query.execute();
//			StringBuilder sb = new StringBuilder();
//			for (DeviceFiles df : entries) {
//				sb.append(df.toString());
//			}
//			pm.close();
//
//			resp.setContentType("text/plain");
//			resp.getWriter().println(sb.toString());
//
//		} else if (testVelocity != null) {
//			KMLGenerator kmlGen = new KMLGenerator();
//			String placemarksDocument = kmlGen
//					.generateDocument("PlacemarkTabs.vm");
//			resp.setContentType("text/plain");
//			resp.getWriter().println(placemarksDocument);
//		} else {
//			//TODO move to DAO
//			javax.jdo.Query query = pm.newQuery("select from  "
//					+ AccessPoint.class.getName());
//			List<AccessPoint> entries = (List<AccessPoint>) query.execute();
//			StringBuilder sb = new StringBuilder();
//			for (AccessPoint ap : entries) {
//				sb.append(ap.toString());
//			}
//			pm.close();
//
//			resp.setContentType("text/plain");
//			resp.getWriter().println(sb.toString());
		}
	}

}

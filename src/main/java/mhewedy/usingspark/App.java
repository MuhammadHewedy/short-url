package mhewedy.usingspark;

import mhewedy.usingspark.data.DataBootstrap;
import mhewedy.usingspark.service.JsonListService;
import mhewedy.usingspark.service.ModelAndViewService;
import mhewedy.usingspark.service.Service;

import org.parse4j.Parse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.template.freemarker.FreeMarkerRoute;

public class App {

	public static void main(String[] args) {

		init();

		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"context.xml");

		
		Service urlResolveService = context.getBean("URLResolveService", Service.class);
		Service apiShortenService = context.getBean("apiShortenService", Service.class);
		JsonListService recentShortenService = context.getBean("recentShortenService", JsonListService.class);
		JsonListService hitDetailsService = context.getBean("hitDetailsService", JsonListService.class);
		ModelAndViewService apiDocService = context.getBean("apiDocService", ModelAndViewService.class);
		ModelAndViewService homeService = context.getBean("homeService", ModelAndViewService.class);
		ModelAndViewService shortenService = context.getBean("shortenService", ModelAndViewService.class);
		ModelAndViewService unShortenService = context.getBean("unShortenService", ModelAndViewService.class);

		Spark.post(new FreeMarkerRoute("/shorten") {
			@Override
			public ModelAndView handle(Request request, Response response) {
				return shortenService.doService(request, response, this);
			}
		});

		Spark.post(new FreeMarkerRoute("/unshorten") {
			@Override
			public ModelAndView handle(Request request, Response response) {
				return unShortenService.doService(request, response, this);
			}
		});

		Spark.get(new Route("/shorten") {
			@Override
			public Object handle(Request request, Response response) {
				response.redirect("/");
				return "";
			}
		});

		Spark.get(new Route("/unshorten") {
			@Override
			public Object handle(Request request, Response response) {
				response.redirect("/");
				return "";
			}
		});

		Spark.get(new FreeMarkerRoute("/apidoc") {
			@Override
			public ModelAndView handle(Request request, Response response) {
				return apiDocService.doService(request, response, this);
			}
		});

		Spark.get(new Route("/api/shorten") {
			@Override
			public Object handle(Request request, Response response) {
				return apiShortenService.doService(request, response);
			}
		});

		Spark.get(new Route("/recent") {
			@Override
			public Object handle(Request request, Response response) {
				return recentShortenService.doService(request, response);
			}
		});

		Spark.get(new Route("/hits") {
			@Override
			public Object handle(Request request, Response response) {
				return hitDetailsService.doService(request, response);
			}
		});

		Spark.get(new Route("/:shortUrl") {
			@Override
			public Object handle(Request request, Response response) {
				return urlResolveService.doService(request, response);
			}
		});

		Spark.get(new FreeMarkerRoute("/") {
			@Override
			public ModelAndView handle(Request request, Response response) {
				return homeService.doService(request, response, this);
			}
		});
	}

	private static void init() {
		try {
			Spark.setPort(Integer.parseInt(System.getenv("PORT")));
			Parse.initialize("S0ryVHKLgL3MII7OmnIRSvzQkCkAAMtvc6BrCKQS", "iG1oZ8OTNBpIJeLzOz6Oj8sS4y5c2J6Pbd6hEsH2");
			DataBootstrap.bootstrap();
			Spark.staticFileLocation("web");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}

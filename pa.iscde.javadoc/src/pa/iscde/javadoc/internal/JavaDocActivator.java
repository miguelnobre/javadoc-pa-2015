package pa.iscde.javadoc.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import pa.iscde.javadoc.service.JavaDocServices;

public class JavaDocActivator implements BundleActivator {

    public static JavaDocActivator instance;

    private ServiceRegistration<JavaDocServices> service;
    private JavaDocServicesImplementation javaDocService;

    @Override
    public void start(final BundleContext context) throws Exception {
	instance = this;
	JavaDocServiceLocator.initialize(context);
	javaDocService = new JavaDocServicesImplementation();
	this.service = context.registerService(JavaDocServices.class, javaDocService, null);
	JavaDocServiceLocator.setService(JavaDocServices.class, javaDocService);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
	instance = null;
	this.service.unregister();
	JavaDocServiceLocator.deinitialize();
    }

    public static JavaDocActivator getInstance() {
	return instance;
    }

    public static JavaDocServicesImplementation getJavaDocService() {
	if( null == instance ) {
	    throw new IllegalStateException();
	}
        return instance.javaDocService;
    }
    
}

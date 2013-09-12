package com.logentries.jenkins;

import java.io.IOException;
import java.io.OutputStream;

import org.kohsuke.stapler.DataBoundConstructor;

import com.logentries.jenkins.Messages;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

public class LogentriesBuildWrapper extends BuildWrapper {

	private final String token;

	/**
	 * Create a new {@link LogentriesBuildWrapper}.
	 * @param token The token for the Logentries' log
	 */
	@DataBoundConstructor
	public LogentriesBuildWrapper(String token) {
		this.token = token;
	}
	
	/**
	 * Gets the Logentries' token
	 * @return The Logentries' token
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream decorateLogger(AbstractBuild build, OutputStream logger) {
		OutputStream decoratedOs = logger;
		try {
			LogentriesWriter logentriesWriter 
				= new AsynchronousLogentriesWriter(new LogentriesTcpTokenWriter(this.token));
			decoratedOs = new LogentriesLogDecorator(logger, logentriesWriter);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			// Programmer error. 
			e.printStackTrace();
		}
		// Should be the wrapped output stream if everything goes ok
		return decoratedOs;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {
		return new Environment() { };
	}

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

	/**
	 * Registers {@link AnsiColorBuildWrapper} as a {@link BuildWrapper}.
	 */
	@Extension
	public static final class DescriptorImpl extends BuildWrapperDescriptor {

		public DescriptorImpl() {
			super(LogentriesBuildWrapper.class);
			load();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDisplayName() {
			return Messages.DisplayName();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isApplicable(AbstractProject<?, ?> item) {
			return true;
		}
	}
}

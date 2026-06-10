package com.ke.bella.openapi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Proxy utility class for configuring and managing global proxy settings
 */
public class ProxyUtils {

	private static final Logger logger = LoggerFactory.getLogger(ProxyUtils.class);

	// Proxy config related constants
	private static String proxyHost = null;
	private static int proxyPort = 0;
	private static Proxy.Type proxyType = Proxy.Type.DIRECT;
	private static final Set<String> proxyDomains = new HashSet<>();

	// System property names
	private static final String PROP_PROXY_HOST = "one-token.proxy.host";
	private static final String PROP_PROXY_PORT = "one-token.proxy.port";
	// value is "socks" or "http"
	private static final String PROP_PROXY_TYPE = "one-token.proxy.type";
	// Multiple domains separated by commas
	private static final String PROP_PROXY_DOMAINS = "one-token.proxy.domains";

	// Static initializer, reads proxy config from environment variables and system properties
	static {
		logger.info("ProxyUtils static initializer started...");
		try {
			initProxyFromSystemProperties();

			if (proxyHost != null && proxyPort > 0) {
				logger.info("Proxy initialized successfully: {}:{} ({}), proxy domains: {}",
					proxyHost, proxyPort, proxyType,
					proxyDomains.isEmpty() ? "All domains" : proxyDomains);
			}
		} catch (Exception e) {
			logger.warn("Failed to initialize proxy config: {}", e.getMessage());
		}
		logger.info("ProxyUtils static initializer completed");
	}

	/**
	 * Initialize proxy config from system properties
	 */
	private static void initProxyFromSystemProperties() {
		String host = System.getProperty(PROP_PROXY_HOST);
		String portStr = System.getProperty(PROP_PROXY_PORT);
		String typeStr = System.getProperty(PROP_PROXY_TYPE);
		String domainsStr = System.getProperty(PROP_PROXY_DOMAINS);

		if (host != null && !host.isEmpty() && portStr != null && !portStr.isEmpty()) {
			try {
				int port = Integer.parseInt(portStr);
				Proxy.Type type = Proxy.Type.DIRECT;

				if ("socks".equalsIgnoreCase(typeStr)) {
					type = Proxy.Type.SOCKS;
				} else if ("http".equalsIgnoreCase(typeStr)) {
					type = Proxy.Type.HTTP;
				}

				String[] domains = null;
				if (domainsStr != null && !domainsStr.isEmpty()) {
					domains = domainsStr.split(",");
				}

				setProxyConfig(host, port, type, domains);
				logger.info("Configured proxy from system properties: {}:{} ({})", host, port, type);
			} catch (NumberFormatException e) {
				logger.warn("Proxy port config error: {}", portStr);
			}
		}
	}

	/**
	 * Set proxy configuration
	 */
	private static void setProxyConfig(String host, int port, Proxy.Type type, String[] domains) {
		proxyHost = host;
		proxyPort = port;
		proxyType = (type == Proxy.Type.HTTP || type == Proxy.Type.SOCKS) ? type : Proxy.Type.DIRECT;

		// Clear and reset domains that need proxy
		proxyDomains.clear();
		if (domains != null && domains.length > 0) {
			for (String domain : domains) {
				if (domain != null && !domain.trim().isEmpty()) {
					proxyDomains.add(domain.trim());
				}
			}
		}
	}

	/**
	 * Check whether the specified URL needs to use proxy
	 *
	 * @param url request URL
	 * @return true if proxy is needed, false otherwise
	 */
	public static boolean shouldUseProxy(String url) {
		// If no proxy configured or URL is empty, do not use proxy
		if (proxyHost == null || proxyPort <= 0 || url == null || url.isEmpty()) {
			return false;
		}

		// If no specific domain is specified, proxy all requests
		if (proxyDomains.isEmpty()) {
			return true;
		}

		// Check if URL contains a domain that needs proxy
		for (String domain : proxyDomains) {
			if (url.contains(domain)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get proxy selector
	 *
	 * @return ProxySelector instance
	 */
	public static ProxySelector getProxySelector() {
		if (proxyHost == null || proxyPort <= 0 || proxyType == Proxy.Type.DIRECT) {
			return ProxySelector.getDefault();
		}

		final Proxy proxy = new Proxy(proxyType, new InetSocketAddress(proxyHost, proxyPort));

		return new ProxySelector() {
			@Override
			public List<Proxy> select(URI uri) {
				String url = uri.toString();
				if (shouldUseProxy(url)) {
					return Collections.singletonList(proxy);
				} else {
					return Collections.singletonList(Proxy.NO_PROXY);
				}
			}

			@Override
			public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
				logger.warn("Proxy connection failed: {} to {}, error: {}", uri, sa, ioe.getMessage());
			}
		};
	}
}

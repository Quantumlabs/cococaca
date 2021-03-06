package org.quantumlabs.cococaca.backend.service.dispatching;

import static org.quantumlabs.cococaca.backend.service.preference.Parameters.URL_FILTER_CONCATENATOR;
import static org.quantumlabs.cococaca.backend.service.preference.Parameters.URL_FILTER_DELIMITER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.quantumlabs.cococaca.backend.Helper;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest.Quantifier;
import org.quantumlabs.cococaca.backend.service.dispatching.RESTRequest.ResourceFilter;

public class DefaultResourceRoutingPolicy implements IResourceRoutingPolicy {

	private static final int _RESOURCE_LOCATOR_IDX = 1;
	private static final int _RESOURCE_FILTERS_IDX = 2;
	private static final int _FILTER_CONDITION_IDX = 0;
	private static final int _FILTER_VALUE_IDX = 1;

	@Override
	public String extractResourceLocator(RESTRequest request) {
		return splitToSegments(request.getURL(), "/")[_RESOURCE_LOCATOR_IDX];
	}

	@Override
	public ResourceFilter[] extractResourceFilters(RESTRequest request) {
		ResourceFilter[] filters = null;
		String[] segments = splitToSegments(request.getURL(),
				URL_FILTER_DELIMITER);
		if (isFilterPresented(segments)) {
			filters = createFilters(segments[_RESOURCE_FILTERS_IDX]);
		}
		return filters;
	}

	private boolean isFilterPresented(String[] segments) {
		return segments.length == 2;
	}

	private ResourceFilter[] createFilters(String filters) {
		String[] filteres = splitToSegments(filters, URL_FILTER_CONCATENATOR);
		final List<ResourceFilter> filterCollector = new ArrayList<>(
				filteres.length);
		collectFilters(filterCollector, filteres);
		return toResourceFilterArray(filterCollector);
	}

	private ResourceFilter[] toResourceFilterArray(
			List<ResourceFilter> filterCollector) {
		ResourceFilter[] returnData = new ResourceFilter[filterCollector.size()];
		filterCollector.toArray(returnData);
		return returnData;
	}

	private void collectFilters(final List<ResourceFilter> filterContainer,
			String... filters) {
		Arrays.asList(filters)
				.stream()
				.forEachOrdered(
						(filter) -> filterContainer.add(createFilter(filter)));
	}

	private ResourceFilter createFilter(String filter) {
		String[] conditionAndValue = splitToSegments(filter, "=");
		return new ResourceFilter(conditionAndValue[_FILTER_CONDITION_IDX],
				conditionAndValue[_FILTER_VALUE_IDX]);
	}

	private String[] splitToSegments(String urlString, String regex) {
		return urlString.split(regex);
	}

	@Override
	public Quantifier extractQuantifier(RESTRequest request) {
		if (Helper.isEmptyString(extractResourceIdentifier(request))) {
			return Quantifier.PLURAL;
		} else {
			return Quantifier.SINGULAR;
		}
	}

	@Override
	public String extractResourceIdentifier(RESTRequest request) {
		// Eg. /Post/1231123?filter=x; /Post?filter=y; /Post
		String identifier = null;
		String[] segments = splitToSegments(request.getURL(), "/");
		// In case, only /ResourceLocator/<id>[?Filter=X] is valid.
		if (segments != null && segments.length == 3) {
			String identifierAndOptionalFilters = segments[2];
			identifier = splitToSegments(identifierAndOptionalFilters, "\\?")[0];
		}
		return identifier;
	}
}

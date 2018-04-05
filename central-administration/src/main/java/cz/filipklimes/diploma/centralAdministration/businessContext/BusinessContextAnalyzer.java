package cz.filipklimes.diploma.centralAdministration.businessContext;

import cz.filipklimes.diploma.centralAdministration.businessContext.exception.CyclicDependencyException;
import cz.filipklimes.diploma.centralAdministration.businessContext.exception.MissingIncludedBusinessContextsException;
import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;

import java.util.*;
import java.util.stream.Collectors;

public class BusinessContextAnalyzer
{

    public static void checkMissingContexts(final Map<BusinessContextIdentifier, BusinessContext> contexts)
        throws MissingIncludedBusinessContextsException
    {
        Set<BusinessContextIdentifier> missingIdentifiers = new HashSet<>();

        for (BusinessContext context : contexts.values()) {
            for (BusinessContextIdentifier included : context.getIncludedContexts()) {
                if (!contexts.containsKey(included)) {
                    missingIdentifiers.add(included);
                }
            }
        }

        if (!missingIdentifiers.isEmpty()) {
            throw new MissingIncludedBusinessContextsException(missingIdentifiers);
        }
    }

    public static void checkCyclicDependency(final Map<BusinessContextIdentifier, BusinessContext> contexts)
        throws CyclicDependencyException
    {
        // Build graph
        Map<String, List<String>> adjacencyList = new HashMap<>();
        Set<String> nodes = contexts.values().stream()
            .map(context -> context.getIdentifier().toString())
            .peek(identifier -> adjacencyList.put(identifier, new ArrayList<>()))
            .collect(Collectors.toSet());

        contexts.values().forEach(context -> {
            context.getIncludedContexts().forEach(included -> {
                adjacencyList.get(context.getIdentifier().toString()).add(included.toString());
            });
        });

        // Run TarjanSolver's algorithm
        new TarjanSolver(nodes, adjacencyList).solve();
    }

    private static final class TarjanSolver
    {

        private final Set<String> nodes;
        private final Map<String, List<String>> adjacencyList;
        private final Stack<String> stack;
        private final Map<String, Integer> lowlink;
        private final Map<String, Integer> index;
        private final Set<String> visited;
        private int precount = 0;

        private TarjanSolver(final Set<String> nodes, final Map<String, List<String>> adjacencyList)
        {

            this.nodes = nodes;
            this.adjacencyList = adjacencyList;
            this.stack = new Stack<>();
            this.lowlink = new HashMap<>();
            this.index = new HashMap<>();
            this.visited = new HashSet<>();
        }

        private void solve() throws CyclicDependencyException
        {
            for (String node : nodes) {
                if (!visited.contains(node)) {
                    dfs(node);
                }
            }
        }

        private void dfs(final String node) throws CyclicDependencyException
        {
            lowlink.put(node, precount);
            precount += 1;
            visited.add(node);
            stack.push(node);

            int min = lowlink.get(node);
            for (String neighbour : adjacencyList.get(node)) {
                if (!visited.contains(neighbour)) {
                    dfs(neighbour);
                }
                min = Math.min(lowlink.get(node), lowlink.get(neighbour));
            }
            if (min < lowlink.get(node)) {
                lowlink.put(node, min);
                return;
            }

            String other;
            int componentCounter = 0;
            do {
                other = stack.pop();
                componentCounter += 1;
                if (componentCounter > 1) {
                    throw new CyclicDependencyException();
                }
                lowlink.put(other, nodes.size());
            } while (!other.equals(node));
        }

    }

}

package com.example.data.repository

data class AiGeneratedResponse(
    val explanation: String,
    val titleTag: String,
    val conceptTag: String,
    val comparisonTable: String,
    val mentalAnchor: String,
    val codeSnippet: String
)

class AiStudyRepository {

    fun generateStudyResponse(query: String, course: String): AiGeneratedResponse {
        val lower = query.lowercase()
        return when {
            lower.contains("dijkstra") || lower.contains("bellman") -> {
                AiGeneratedResponse(
                    explanation = "Shortest path algorithms solve for minimal cumulative weight. The primary divergence lies in how greedily optimal choices are locked in.",
                    titleTag = "Algorithmic Paradigm Contrast",
                    conceptTag = "Concept Map",
                    comparisonTable = "Dijkstra's (Greedy)|O((V + E) log V)|Fails on negative weights###Bellman-Ford (DP)|O(V × E)|Detects negative cycles",
                    mentalAnchor = "Dijkstra assumes paths never get cheaper once closed. A negative edge breaks this contract by creating a retroactive discount!",
                    codeSnippet = "# Relax edges |V| - 1 times\nfor _ in range(V - 1):\n    for u, v, w in edges:\n        if dist[u] + w < dist[v]:\n            dist[v] = dist[u] + w"
                )
            }
            lower.contains("memory") || lower.contains("page") || lower.contains("virtual") -> {
                AiGeneratedResponse(
                    explanation = "Virtual memory decouples logical address space from physical memory. Paging maps fixed-size virtual blocks (Pages) to physical blocks (Frames) via the Memory Management Unit (MMU).",
                    titleTag = "Memory Management Architecture",
                    conceptTag = "OS Core",
                    comparisonTable = "Standard Paging|Fast lookup via TLB|High page table memory overhead###Inverted Page Tables|One entry per physical frame|Requires hash lookup / search penalty",
                    mentalAnchor = "Paging is like an index in a textbook: you look up the concept (virtual page) to jump directly to the physical printed page (frame).",
                    codeSnippet = "// Address Translation Formula:\n// Virtual Address = (Page Number << Page_Offset_Bits) | Offset\nPhysicalAddress = (PageTable[PageNumber].frameNumber << Page_Offset_Bits) | Offset;"
                )
            }
            lower.contains("compiler") || lower.contains("lr") || lower.contains("parsing") -> {
                AiGeneratedResponse(
                    explanation = "LR(1) parsers are deterministic bottom-up shift-reduce parsers that look ahead by 1 token to disambiguate shifts from reductions without backtracking.",
                    titleTag = "Deterministic Grammar Parsing",
                    conceptTag = "Automata",
                    comparisonTable = "LR(0) Parser|No lookahead in state items|Suffers shift-reduce conflicts###LR(1) Parser|Explicit 1-token lookahead|Powerful but larger state machine",
                    mentalAnchor = "Shift means 'push token onto stack to wait for context'. Reduce means 'we recognize this pattern as a production rule!'.",
                    codeSnippet = "# LR parser action loop\nwhile not accepted:\n    action = parse_table[state, current_token]\n    if action.startswith('s'): shift(action.state)\n    elif action.startswith('r'): reduce(action.rule)"
                )
            }
            lower.contains("master theorem") || lower.contains("recurrence") -> {
                AiGeneratedResponse(
                    explanation = "Master Theorem solves recurrences of the form T(n) = aT(n/b) + f(n). It compares the driving function f(n) with the watershed polynomial n^(log_b a).",
                    titleTag = "Recurrence Asymptotics",
                    conceptTag = "Complexity",
                    comparisonTable = "Case 1: Tree Heavy|f(n) = O(n^(log_b a - ε))|Theta(n^(log_b a))###Case 2: Balanced|f(n) = Theta(n^(log_b a) log^k n)|Theta(n^(log_b a) log^(k+1) n)",
                    mentalAnchor = "Case 1: Leaves do all the work. Case 2: Work is evenly spread across all tree levels. Case 3: Root dominates everything.",
                    codeSnippet = "// Binary Search Recurrence:\n// T(n) = T(n/2) + O(1) -> a=1, b=2, k=0\n// Watershed = n^(log_2 1) = n^0 = 1\n// Case 2 applies -> T(n) = Theta(log n)"
                )
            }
            else -> {
                AiGeneratedResponse(
                    explanation = "Analysis for '$query' in course module $course. High-yield summary extracted from curriculum and exam blueprints.",
                    titleTag = "Academic Concept Analysis",
                    conceptTag = "Syllabus Aligned",
                    comparisonTable = "Theoretical Paradigm|Formal proof & bounds|Focus on edge cases###Engineering Application|Memory & cache efficiency|Real-world implementation",
                    mentalAnchor = "Anchor key principles first, then verify with sample test traces and time/space complexity derivations.",
                    codeSnippet = "// Concept verification snippet\nvoid analyzeConcept() {\n    // Optimal algorithmic flow\n    processInput();\n}"
                )
            }
        }
    }
}

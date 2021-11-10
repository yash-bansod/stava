// Check handling of inlining case

public class Main {
	public static void main(String[] args) {
		Node A = new Node();
		Node B = new Node();
		Node C = func(B);
	}

	public static Node func(Node p1) {
		Node D = new Node();
		return D;
	}
}

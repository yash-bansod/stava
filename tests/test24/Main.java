// Check handling of inlining case

public class Main {
	public static void main(String[] args) {
		Node A = new Node();
		Node B = new Node();
		func(B);
	}

	public static void func(Node p1) {
		Node C = new Node();
		Node D = new Node();
		D.n = C;
	}
}

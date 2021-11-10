// Check handling of inlining case

public class Main {
	public static void main(String[] args) {
		Node A = new Node();
		Node B = new Node();
		func(A, B);
	}

	public static void func(Node p1, Node p2) {
		Node C = new Node();
		Node D = new Node();
		p1.n = D;	
		p2.n = C;
	}
}

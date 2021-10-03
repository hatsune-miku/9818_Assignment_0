package a0;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PartitionsTest {
	@Test
	void testPartitions0() {
		assertEquals(1, Partitions.countPartitions(0, 0));
	}

	@Test
	void testPartition1() {
		assertEquals(1, Partitions.countPartitions(4, 0));
		assertEquals(4, Partitions.countPartitions(4, 1));
		assertEquals(6, Partitions.countPartitions(4, 2));
		assertEquals(4, Partitions.countPartitions(4, 3));
		assertEquals(1, Partitions.countPartitions(4, 4));
	}

	/*
	// C(x,y) = x! / y!

	// C(n, m)
	private int collectionFast(int n, int m) {
		if (m > n) {
			throw new IllegalArgumentException();
		}
		if (m < 0) {
			throw new IllegalArgumentException();
		}
		// m <= n
		int base = Math.min(m, n - m);
		return fact(n, base) / (fact(m, base) * fact(n - m, base));
	}

	private int fact(int x, int downTo) {
		int ans = 1;
		while (x > downTo) {
			ans *= x--;
		}
		return ans;
	}

	private int fact(int x) {
		return fact(x, 1);
	}
	 */
}

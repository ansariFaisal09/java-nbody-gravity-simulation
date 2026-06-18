# Java N-Body Gravity Simulation

A Core Java physics engine that simulates the gravitational pull between thousands of objects. I built this to practice advanced data structures and test the limits of Java multithreading.

## The Bottleneck: Why O(N^2) Fails
Calculating gravity for every particle against every other particle is incredibly slow. For 15,000 particles, a brute-force approach requires over 225 million calculations per frame. 

I initially tried to speed this up using a standard Java Thread Pool. However, having my 8 CPU threads simultaneously fighting to read the same array in memory caused massive cache thrashing, which actually made the simulation run *slower*.

## The Fix: Barnes-Hut & QuadTrees
To solve this, I rewrote the engine using the **Barnes-Hut algorithm**. 

The engine divides the 2D space into a recursively subdividing QuadTree. If a cluster of particles is far away, the algorithm stops looking at the individual particles and treats the entire cluster as a single, massive "Center of Mass." 

By combining this $O(N \log N)$ algorithm with an 8-thread processing pool, the execution time dropped by 97%.

### Benchmarks (15,000 Particles / 8 Hardware Threads)
| Architecture | Time Complexity | Time per Frame |
| :--- | :---: | :---: |
| Single-Threaded | O(N^2) | ~2052 ms |
| Multithreaded (Brute Force) | O(N^2) | ~3061 ms |
| **Barnes-Hut + Multithreading** | **O(N log N)** | **~74 ms** |

## Tech Stack
* **Language:** Core Java
* **Concurrency:** `java.util.concurrent.ExecutorService` (Fixed Thread Pool)
* **Algorithms:** Barnes-Hut Optimization, QuadTree spatial partitioning

## How to Run Locally
1. Clone this repository.
2. Open the project in your IDE (IntelliJ IDEA recommended).
3. Ensure you are using Java 8 or higher.
4. Run `src/main/java/com/ansari/simulation/Main.java`.
5. The performance benchmarks will print directly to your console.

---
**Author:** Faisal Ansari  
**Connect:** [LinkedIn](https://linkedin.com/in/faisal-ansari) | faisalraj9193@gmail.com

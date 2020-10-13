import java.util.ArrayList;
import java.util.Scanner;

public class AditiSejal_2019228_FinalAssignment {
	
	static class Direct {
		
		long M;	//size of main memory
		long CL; //number of cache lines
		long B;	//block size (in words)
		long S; //size of cache (calculated in bytes)
		long N; //number of blocks
		int p1;
		int p2;
		int n;
		int w;
		dmcache[] l1;
		dmcache[] l2;
		
		class dmcache {
			String tag;	
			int vbit;
			int index;	
			int dbit; 	//dirty bit
			int data[];
			String address;
			
			dmcache(){
				vbit = 0;
				data = new int[(int)B];
				dbit = 0;
				index = 0;
				address = null;
			}	
			
			public void show(int p) {
				String tagger = String.format("%1$-" + 2*p + "s", tag);
				String indexer = String.format("%1$-" + (CL%10) + "s", index+"");
				System.out.print(tagger + " " + indexer);
				for(int i = 0; i<B; i++) {
					System.out.print("    " + data[i]);
				}
				System.out.println();
			}
		}
		
		Direct(long mm, long ccll, long bb){
			M = mm;
			B = bb;
			CL = ccll;
			N = M/B;
			S = CL*B*(16/8);
			p1 = powerofII(CL/2);
			p2 = powerofII(CL);
			n = powerofII(M);
			w = powerofII(B);
			l1 = new dmcache[(int)CL/2];
			l2 = new dmcache[(int)CL];
			for(int i = 0; i<CL/2; i++) {
				l1[i] = new dmcache();
				l1[i].index = i;
			}
			for(int i = 0; i<CL; i++) {
				l2[i] = new dmcache();
				l2[i].index = i;
			}
		}
		
		public void l1printer() {
			for(int i = 0; i<CL/2; i++) {
				l1[i].show(p1);
			}
		}
		
		public void l2printer() {
			for(int i = 0; i<CL; i++) {
				l2[i].show(p2);
			}
		}
		
		public void reader(String address) {
			String t1 = address.substring(0,n-w-p1);
			String cl1 = address.substring(n-w-p1,n-w);
			String t2 = address.substring(0,n-w-p2);
			String cl2 = address.substring(n-w-p2,n-w);
			String word = address.substring(n-w);
			int idx1 = todecimal(cl1);
			int idx2 = todecimal(cl2);
			if(l1[idx1].vbit == 0) {		//L1 cache empty
				System.out.println("L1: Cache Read Miss");	//write here first
				if(l2[idx2].vbit == 0) {		//look in L2
					System.out.println("L2: Cache Read Miss");		//L2 cache empty
				}
				else {					
					if(l2[idx2].tag.contentEquals(t2)) {
						//proper read
						System.out.println("L2: Cache Read Hit");
						System.out.println("Data at given address is:");		//L2 read successfully
						System.out.println(l2[idx2].data[todecimal(word)]);
						l1[idx1].vbit = 1;
						l1[idx1].dbit = l2[idx2].dbit;
						l1[idx1].tag = t1;
						for(int u=0;u<B;u++) {
							l1[idx1].data[u] = l2[idx2].data[u];
						}
						l2[idx2].vbit = 0;
						for(int y=0;y<B;y++) {
							l2[idx2].data[y] = 0;
						}
					}
					else {			//modify for L2
						System.out.println("L2: Cache Read Miss");
					}				
				}
			}
			else {						//L1 cache full
				if(l1[idx1].tag.contentEquals(t1)) {
					System.out.println("L1: Cache Read Hit");
					System.out.println("Data at given address is:");		//L1 read successfully
					System.out.println(l1[idx1].data[todecimal(word)]);
				}
				else {
					System.out.println("L1: Cache Read Miss");	//replacement modify
					if(l2[idx2].vbit == 0) {
						System.out.println("L2: Cache Read Miss");		//L2 cache empty
					}
					else {
						if(l2[idx2].tag.contentEquals(t2)) {	//proper read
							System.out.println("L2: Cache Read Hit");
							System.out.println("Data at given address is:");		//L2 read successfully
							System.out.println(l2[idx2].data[todecimal(word)]);
							dmcache obj4l1 = new dmcache();
							obj4l1.vbit = 1;
							obj4l1.dbit = l2[idx2].dbit;
							obj4l1.tag = t1;
							obj4l1.index = idx1;
							for(int u=0;u<B;u++) {
								obj4l1.data[u] = l2[idx2].data[u];
								l2[idx2].data[u] = 0;
							}
							l2[idx2].vbit = 0;
							dmcache movetol2 = new dmcache();
							movetol2.vbit = 1;
							movetol2.dbit = l1[idx1].dbit;
							String tag4l24l1 = (l1[idx1].tag + binaryconverter(l1[idx1].index,p1)).substring(0,n-w-p2);
							String inx4l24l1 = (l1[idx1].tag + binaryconverter(l1[idx1].index,p1)).substring(n-w-p2,n-w);
							movetol2.tag = tag4l24l1;
							movetol2.index = todecimal(inx4l24l1);
							for(int u=0;u<B; u++) {
								movetol2.data[u] = l1[idx1].data[u];
							}
							System.out.printf("L1: Moved block no %d to L2 cache\n", todecimal(movetol2.tag + binaryconverter(movetol2.index,p2)));
							if(l2[movetol2.index].vbit == 0) {
								l2[movetol2.index].vbit = 1;
								l2[movetol2.index].dbit = movetol2.dbit;
								l2[movetol2.index].tag = movetol2.tag;
								for(int u=0; u<B; u++) {
									l2[movetol2.index].data[u] = movetol2.data[u];
								}
								System.out.printf("L2: Moved block no %d to cache\n", todecimal(movetol2.tag + binaryconverter(movetol2.index,p2)));
							}
							else {
								int blocklost = todecimal(l2[movetol2.index].tag + binaryconverter(movetol2.index,p2));
								l2[movetol2.index].vbit = 1;
								l2[movetol2.index].dbit = movetol2.dbit;
								l2[movetol2.index].tag = movetol2.tag;
								for(int u=0; u<B; u++) {
									l2[movetol2.index].data[u] = movetol2.data[u];
								}
								System.out.printf("L2: Replaced block no %d with block no %d in cache\n", blocklost,todecimal(movetol2.tag + binaryconverter(movetol2.index,p2)));
							}									
							l1[idx1].tag = obj4l1.tag;
							l1[idx1].vbit = 1;
							l1[idx1].dbit = obj4l1.dbit;
							for(int u=0;u<B;u++) {
								l1[idx1].data[u] = obj4l1.data[u];
							}
						}
						else {				//modify for L2
							System.out.println("L2: Cache Read Miss");
						}				
					}
					}
				}
		}
		
		public void writer(String address, int data) {
			String t1 = address.substring(0,n-w-p1);
			String cl1 = address.substring(n-w-p1,n-w);
			String t2 = address.substring(0,n-w-p2);
			String cl2 = address.substring(n-w-p2,n-w);
			String word = address.substring(n-w);
			int idx1 = todecimal(cl1);
			int idx2 = todecimal(cl2);
			if(l1[idx1].vbit == 0) {				//L1 cache empty so just write
				l1[idx1].vbit = 1;
				l1[idx1].tag = t1;
				l1[idx1].data[todecimal(word)] = data;
				l1[idx1].dbit = 0;
				l1[idx1].address = address;
				System.out.println("L1: Cache Write Miss");
				if(l2[idx2].vbit == 0) {
					System.out.println("L2: Cache Write Miss");		//L2 cache empty
				}
				else {
					if(l2[idx2].tag.contentEquals(t2)) {	//L2 read successfully
						if(l2[idx2].dbit == 0) {
							System.out.println("L2: Cache Write Hit");	
							l2[idx2].vbit = 0;		//remove from L2
						}
						else if(l2[idx2].dbit == 1){
							System.out.println("L2: Cache Write Hit");
							for(int f=0;f<B;f++) {
								if(f!=todecimal(word)) {
									l1[idx1].data[f] = l2[idx2].data[f];
								}
							}
							l1[idx1].dbit = 1;
							l2[idx2].vbit = 0;
						}
					}
					else {
						System.out.println("L2: Cache Write Miss");
					}				
				}
				System.out.println("L1: Data written to cache successfully");
			}
			else {								//cache full so replace
				if(l1[idx1].tag.contentEquals(t1)) {			//cache hit
					l1[idx1].data[todecimal(word)] = data;
					l1[idx1].dbit = 1;
					l1[idx1].address = address;
					System.out.println("L1: Cache Write Hit");
					if(l2[idx2].vbit == 0) {
						System.out.println("L2: Cache Write Miss");		//L2 cache empty
					}
					else {
						if(l2[idx2].tag.contentEquals(t2)) {	//L2 read successfully
							if(l2[idx2].dbit == 0) {
								System.out.println("L2: Cache Write Hit");	
								l2[idx2].vbit = 0;		//remove from L2
							}
							else if(l2[idx2].dbit == 1){
								System.out.println("L2: Cache Write Hit");
								for(int f=0;f<B;f++) {
									if(f!=todecimal(word)) {
										l1[idx1].data[f] = l2[idx2].data[f];
									}
								}
								l1[idx1].dbit = 1;
								l2[idx2].vbit = 0;
								System.out.printf("L2: Block no %d removed from cache\n", todecimal(l2[idx2].tag) + idx2);
							}
						}
						else {
							System.out.println("L2: Cache Write Miss");
						}
					}
					System.out.println("L1: Data updated in cache successfully");
				}
				else {							//cache miss
					//tag does not match
					System.out.println("L1: Cache Write Miss");				
					if(l2[idx2].vbit == 0) {
						System.out.println("L2: Cache Write Miss");
						dmcache obj1 = new dmcache();
						obj1.vbit = l1[idx1].vbit;
						obj1.dbit = l1[idx1].dbit;
						obj1.address = l1[idx1].address;
						String tag4l24l1 = (l1[idx1].tag + binaryconverter(l1[idx1].index,p1)).substring(0,n-w-p2);
						String inx4l24l1 = (l1[idx1].tag + binaryconverter(l1[idx1].index,p1)).substring(n-w-p2,n-w);
						obj1.tag = tag4l24l1;	
						obj1.index = todecimal(inx4l24l1);
						for(int u=0;u<B;u++) {
							obj1.data[u] = l1[idx1].data[u];
						}
						if(l2[todecimal(inx4l24l1)].vbit == 0) { 
							System.out.printf("L2: Loading block no %d into cache from L1\n", todecimal((l1[idx1].tag)+binaryconverter(l1[idx1].index,p1)));
						}
						else {
							System.out.printf("L2: Replacing block no %d with block no %d in cache\n", todecimal(l2[obj1.index].tag+binaryconverter(l2[obj1.index].index,p2)) , todecimal(obj1.tag+binaryconverter(obj1.index,p2)));
						}
						idx2 = obj1.index;
						l2[idx2].vbit = obj1.vbit;
						l2[idx2].dbit = obj1.vbit;
						l2[idx2].tag = obj1.tag;
						for(int u=0;u<B;u++) {
							l2[idx2].data[u] = obj1.data[u];
						}
						l1[idx1].vbit = 1;
						l1[idx1].dbit = 0;
						l1[idx1].tag = t1;
						for(int u=0;u<B;u++) {
							l1[idx1].data[u] = 0;
						}
						l1[idx1].data[todecimal(word)] = data;
						System.out.println("L1: Data overwritten in cache successfully");
					}
					else {
						if(l2[idx2].tag.contentEquals(t2)) {
							System.out.println("L2: Cache Write Hit");
							l2[idx2].data[todecimal(word)] = data;
							dmcache temp = new dmcache();
							temp.vbit = l2[idx2].vbit;
							temp.dbit = 1;
							String tag4l14l2 = (l2[idx2].tag + binaryconverter(l2[idx2].index,p2)).substring(0,n-w-p1);
							String inx4l14l2 = (l2[idx2].tag + binaryconverter(l2[idx2].index,p2)).substring(n-w-p1,n-w);
							temp.tag = tag4l14l2;
							temp.index = todecimal(inx4l14l2);
							for(int u=0;u<B;u++) {
								temp.data[u] = l2[idx2].data[u];
							}
							l2[idx2].vbit  = 0;
							for(int u=0;u<B;u++) {
								l2[idx2].data[u] = 0;
							}
							String tag4l24l1 = (l1[idx1].tag + binaryconverter(l1[idx1].index,p1)).substring(0,n-w-p2);
							String inx4l24l1 = (l1[idx1].tag + binaryconverter(l1[idx1].index,p1)).substring(n-w-p2,n-w);
							idx2 = todecimal(inx4l24l1);
							l2[idx2].vbit = l1[idx1].vbit;
							l2[idx2].dbit = l1[idx1].dbit;
							
							l2[idx2].tag = tag4l24l1;
							for(int u=0;u<B;u++) {
								l2[idx2].data[u] = l1[idx1].data[u];
							}
							System.out.printf("L2: Replacing block no %d with block no %d\n", todecimal(t1 + binaryconverter(idx1,p1)), todecimal(l1[idx1].tag + binaryconverter(idx1,p1)));
							l1[idx1].vbit = temp.vbit;
							l1[idx1].dbit = temp.dbit;
							l1[idx1].tag = temp.tag;
							l1[idx1].index = temp.index;
							for(int u=0;u<B;u++) {
								l1[idx1].data[u] = temp.data[u];
							}
							System.out.println("L1: Data overwritten in cache successfully");
						}
						else {
							System.out.println("L2: Cache Write Miss");
							int moved = todecimal(l1[idx1].tag + binaryconverter(l1[idx1].index,p1));
							String inx4l24l1 = (l1[idx1].tag + binaryconverter(l1[idx1].index,p1)).substring(n-w-p2,n-w);
							idx2 = todecimal(inx4l24l1);
							l2[idx2].vbit = l1[idx1].vbit;
							l2[idx2].dbit = l1[idx1].dbit;
							String tag4l24l1 = (l1[idx1].tag + binaryconverter(l1[idx1].index,p1)).substring(0,n-w-p2);
							l2[idx2].tag = tag4l24l1;
							l2[idx2].index = todecimal(inx4l24l1);
							for(int u=0;u<B;u++) {
								l2[idx2].data[u] = l1[idx1].data[u];
							}
							l1[idx1].vbit = 1;
							l1[idx1].dbit = 0;
							l1[idx1].tag = t1;
							for(int u=0;u<B;u++) {
								l1[idx1].data[u] = 0;
							}
							l1[idx1].data[todecimal(word)] = data;
							System.out.printf("L1: Moving block no %d to L2 cache\n", moved);
							System.out.println("L1: Data overwritten in cache successfully");					
						}
					}
				}
			}
		}
	}
			
	static class Associative {
		
		long M;	//size of main memory
		long CL; //number of cache lines
		long B;	//block size (in words)
		long S; //size of cache (in words)
		long N; //number of blocks
		int n;
		int w;
		ArrayList<dmcache> l1;
		ArrayList<dmcache> l2;
		int p1;	//pointer for l1
		int p2;	//pointer for l2
		int gt;	//global time
		
		class dmcache {
			String tag;	
			int vbit;
			int dbit; 	//dirty bit
			int time;
			int data[];
			
			dmcache(){
				vbit = 0;
				data = new int[(int)B];
				dbit = 0;
				time = 0;
			}	
			
			public void show() {
				String tagger = String.format("%1$-" + (n-w) + "s", tag);
				String timer = String.format("%1$-" + 10 + "s", time);
				System.out.print(tagger + "  " + timer);
				for(int i = 0; i<B; i++) {
					System.out.print("    " + data[i]);
				}
				System.out.println();
			}
		}
		
		@SuppressWarnings("unchecked")
		Associative(long mm, long ccll, long bb){
			M = mm;
			B = bb;
			CL = ccll;
			N = M/B;
			S = CL*B*(16/8);
			n = powerofII(M);
			w = powerofII(B);
			l1 = new ArrayList();
			l2 = new ArrayList();
			p1 = 0;
			p2 = 0;
			gt = 0;
		}
		
		public void l1printer() {
			int i;
			for(i = 0; i<l1.size(); i++) {
				l1.get(i).show();
			}
			while(i<CL/2) {
				System.out.println("empty");
				i+=1;
			}			
		}
		
		public void l2printer() {
			int i;
			for(i = 0; i<l2.size(); i++) {
				l2.get(i).show();
			}
			while(i<CL) {
				System.out.println("empty");
				i+=1;
			}
		}	
		
		public void reader(String address) {
			String t = address.substring(0,n-w);
			String word = address.substring(n-w);
			gt += 1;
			boolean foundin1 = false;
			boolean foundin2 = false;
			for(int i = 0; i<l1.size(); i++) {
				if(l1.get(i).tag.contentEquals(t)) {
					System.out.println("L1: Cache Read Hit");
					l1.get(i).time = gt;
					System.out.println("L1: Data at given address is:");
					System.out.println(l1.get(i).data[todecimal(word)]);
					foundin1 = true;
					break;
				}
			}
			if(!foundin1) {
				System.out.println("L1: Cache Read Miss");
				for(int j=0;j<l2.size();j++) {
					if(l2.get(j).tag.contentEquals(t)) {
						System.out.println("L2: Cache Read Hit");
						l2.get(j).time = gt;
						System.out.println("L2: Data at given address is:");
						System.out.println(l2.get(j).data[todecimal(word)]);
						foundin2 = true;
						dmcache obj = new dmcache();
						obj.vbit = 1;
						obj.dbit = l2.get(j).dbit;
						obj.time  = gt;
						obj.tag = l2.get(j).tag;
						for(int u=0;u<B;u++) {
							obj.data[u] = l2.get(j).data[u];
						}
						if(l1.size()==(CL/2)) {
							int minidx = 0;
							for(int k=0;k<l1.size();k++) {
								if(l1.get(k).time < l1.get(minidx).time) {
									minidx = k;
								}
							}
							int blockreplaced = todecimal(l1.get(minidx).tag);
							dmcache moved = new dmcache();
							moved.vbit = 1;
							moved.dbit = l1.get(minidx).dbit;
							moved.tag = l1.get(minidx).tag;
							moved.time = l1.get(minidx).time;
							for(int u=0;u<B;u++) {
								moved.data[u] = l1.get(minidx).data[u];
							}
							l1.remove(minidx);
							l1.add(obj);
							l2.remove(j);
							l2.add(moved);
							System.out.printf("L1: Moved block no %d to L2 cache\n", blockreplaced);
							System.out.printf("L1: Replaced block no %d with block no %d in cache\n", blockreplaced, todecimal(t));
						}
						else {
							l1.add(obj);
							l2.remove(obj);
							System.out.printf("L2: Moved block no %d to L1 cache\n", todecimal(obj.tag));
							System.out.println("L1: Data written to cache successfully");
						}
						break;
					}
				}
				if(!foundin2) {
					System.out.println("L2: Cache Read Miss");
				}
			}			
		}
		
		public void writer(String address, int data) {
			String t = address.substring(0,n-w);
			String word = address.substring(n-w);
			gt += 1;
			if(l1.size() == 0 && l2.size() == 0) {
				System.out.println("L1: Cache Write Miss");
				System.out.println("L2: Cache Write Miss");
				dmcache obj = new dmcache();
				obj.tag = t;
				obj.data[todecimal(word)] = data;
				obj.time = gt;
				obj.dbit = 0;
				obj.vbit = 1;
				l1.add(obj);
				p1 += 1;
				System.out.println("L1: Data written to cache successfully");
			}
			else {
				boolean found = false;
				for(int i = 0; i<l1.size(); i++) {
					if(l1.get(i).tag.contentEquals(t)) {
						System.out.println("L1: Cache Write Hit");
						l1.get(i).time = gt;
						l1.get(i).dbit = 1;
						l1.get(i).vbit = 1;
						l1.get(i).data[todecimal(word)] = data;
						found = true;
						System.out.println("L1: Data updated in cache successfully");
						break;
					}
				}
				boolean foundin2 = false;
				for(int j=0; j<l2.size(); j++) {
					if(l2.get(j).tag.contentEquals(t)) {
						foundin2 = true;
						if(found) {
							l2.remove(j);
							break;
						}
						else {
							System.out.println("L1: Cache Write Miss");
							System.out.println("L2: Cache Write Hit");
							dmcache obj = new dmcache();
							obj.vbit = 1;
							obj.dbit = 1;
							obj.time  = gt;
							obj.tag = l2.get(j).tag;
							for(int u=0;u<B;u++) {
								obj.data[u] = l2.get(j).data[u];
							}
							obj.data[todecimal(word)] = data;
							if(l1.size()==(CL/2)) {
								int minidx = 0;
								for(int k=0;k<l1.size();k++) {
									if(l1.get(k).time < l1.get(minidx).time) {
										minidx = k;
									}
								}
								int blockreplaced = todecimal(l1.get(minidx).tag);
								dmcache moved = new dmcache();
								moved.vbit = 1;
								moved.dbit = l1.get(minidx).dbit;
								moved.tag = l1.get(minidx).tag;
								moved.time = l1.get(minidx).time;
								for(int u=0;u<B;u++) {
									moved.data[u] = l1.get(minidx).data[u];
								}
								l1.remove(minidx);
								l1.add(obj);
								l2.remove(obj);
								l2.add(moved);
								System.out.printf("L1: Moved block no %d to L2 cache\n", blockreplaced);
								System.out.printf("L1: Replaced block no %d with block no %d in cache\n", blockreplaced, todecimal(t));
							}
							else {
								l1.add(obj);
								l2.remove(obj);
								System.out.printf("L2: Moved block no %d to L1 cache\n", todecimal(obj.tag));
								System.out.println("L1: Data written to cache successfully");
							}
						}
						break;
					}
				}
					if(!foundin2 && !found) {
						System.out.println("L1: Cache Write Miss");
						System.out.println("L2: Cache Write Miss");
						dmcache obj = new dmcache();
						obj.vbit = 1;
						obj.dbit = 0;
						obj.time = gt;
						obj.tag = t;
						obj.data[todecimal(word)] = data;
						if(l1.size() == (CL/2)) {
							int minidx = 0;
							for(int k=0;k<l1.size();k++) {
								if(l1.get(k).time < l1.get(minidx).time) {
									minidx = k;
								}
							}
							int blockreplaced = todecimal(l1.get(minidx).tag);
							dmcache moved = new dmcache();
							moved.vbit = 1;
							moved.dbit = l1.get(minidx).dbit;
							moved.tag = l1.get(minidx).tag;
							moved.time = l1.get(minidx).time;
							for(int u=0;u<B;u++) {
								moved.data[u] = l1.get(minidx).data[u];
							}
							l1.remove(minidx);
							l1.add(obj);
							System.out.println("L1: Data written to cache successfully");
							System.out.printf("L1: Moved block no %d to L2 cache\n", blockreplaced);
							if(l2.size() == CL) {
								int min2 = 0;
								for(int k=0;k<l2.size();k++) {
									if(l2.get(k).time < l2.get(min2).time) {
										min2 = k;
									}
								}
								int blocklost = todecimal(l2.get(min2).tag);
								l2.remove(blocklost);
								l2.add(moved);
								System.out.printf("L2: Replaced block no %d with block no %d\n", blocklost, todecimal(moved.tag));
							}
							else {
								l2.add(moved);				
							}
						}
						else {
							l1.add(obj);
							p1+=1;
							System.out.println("L1: Data written to cache successfully");
						}
					}
				}
		}		
	}

	static class SetAssociative {
		
		long M;	//size of main memory
		long CL; //number of cache lines
		long B;	//block size (in words)
		long S; //size of cache (in words)
		long N; //number of blocks
		int v; //v cache lines for each set
		int n;
		int w;
		int d1; //size of set
		int d2;
		int gt;
		int nsets1;	//no of sets
		int nsets2;
		ArrayList<dmcache>[] l1;
		ArrayList<dmcache>[] l2;
		
		class dmcache {
			String tag;	
			int vbit;
			int set;	//don't need only need for print
			int dbit; 	//dirty bit
			int data[];
			int time;
			
			dmcache(){
				vbit = 0;
				data = new int[(int)B];
				dbit = 0;
				set = 0;
				time = 0;
			}	
			
			public void show(int p) {
				String tagger = String.format("%1$-" + 2*p + "s", tag);
				String setter = String.format("%1$-" + (CL%10) + "s", set + "");
				System.out.print(tagger + " " + setter);
				for(int i = 0; i<B; i++) {
					System.out.print("    " + data[i]);
				}
				System.out.println();
			}
	}
		
		@SuppressWarnings("unchecked")
		SetAssociative(long mm, long ccll, long bb, int vv){
			M = mm;
			B = bb;
			CL = ccll;
			N = M/B;
			v = vv;
			S = CL*B*(16/8);
			nsets2 = (int)CL/vv;
			nsets1 = (int)CL/(2*vv);
			n = powerofII(M);
			w = powerofII(B);
			d1 = powerofII(nsets1);
			d2 = powerofII(nsets2);
			gt = 0;
			l1 = new ArrayList[nsets1];
			l2 = new ArrayList[nsets2];
			for(int i1=0;i1<nsets1;i1++) {
				l1[i1] = new ArrayList<dmcache>();
			}
			for(int i2=0;i2<nsets2;i2++) {
				l2[i2] = new ArrayList<dmcache>();
			}			
		}
		
		public void l1printer() {
			for(int j = 0; j<nsets1; j++) {
				int i;
				for(i = 0; i<l1[j].size(); i++) {
					l1[j].get(i).show(d1);
				}
				while(i<v) {
					System.out.println("empty");
					i+=1;
				}			
			}
		}
		
		public void l2printer() {
			for(int j = 0; j<nsets2; j++) {
				int i;
				for(i = 0; i<l2[j].size(); i++) {
					l2[j].get(i).show(d2);
				}
				while(i<v) {
					System.out.println("empty");
					i+=1;
				}			
			}
		}
		
		public void reader(String address) {
			String t1 = address.substring(0,n-w-d1);
			String t2 = address.substring(0,n-w-d2);
			String idx1 = address.substring(n-w-d1,n-w);
			String idx2 = address.substring(n-w-d2,n-w);
			String word = address.substring(n-w);
			int s1 = todecimal(idx1);
			int s2 = todecimal(idx2);
			gt += 1;
			boolean foundin1 = false;
			boolean foundin2 = false;
			for(int i = 0; i<l1[s1].size(); i++) {
				if(l1[s1].get(i).tag.contentEquals(t1)) {
					System.out.println("L1: Cache Read Hit");
					foundin1 = true;
					l1[s1].get(i).time = gt;
					System.out.println("L1: Data at given address is:");
					System.out.println(l1[s1].get(i).data[todecimal(word)]);
					break;
				}
			}
			if(!foundin1) {
				System.out.println("L1: Cache Read Miss");
				for(int i = 0 ;i<l2[s2].size();i++) {
					if(l2[s2].get(i).tag.contentEquals(t2)) {
						System.out.println("L2: Cache Read Hit");
						foundin2 = true;
						l2[s2].get(i).time = gt;
						System.out.println("L1: Data at given address is");
						System.out.println(l2[s2].get(i).data[todecimal(word)]);
						dmcache obj = new dmcache();
						obj.vbit = 1;
						obj.dbit = l2[s2].get(i).dbit;
						obj.time = gt;
						obj.tag = t1;
						obj.set = s1;
						for(int u=0; u<B; u++) {
							obj.data[u] = l2[s2].get(i).data[u];
						}
						if(l1[s1].size() == v) {
							int minidx = 0;
							for(int y=0;y<l1[s1].size();y++) {
								if(l1[s1].get(y).time < l1[s1].get(minidx).time) {
									minidx = y;
								}
							}
							dmcache movetol2 = new dmcache();
							movetol2.vbit = 1;
							movetol2.dbit = l1[s1].get(minidx).dbit;
							movetol2.time = l1[s1].get(minidx).time;
							movetol2.tag = (l1[s1].get(minidx).tag + binaryconverter(s1,d1)).substring(0,n-w-d2);
							movetol2.set = todecimal((l1[s1].get(minidx).tag + binaryconverter(s1,d1)).substring(n-w-d2,n-w));
							for(int u=0;u<B;u++) {
								movetol2.data[u] = l1[s1].get(minidx).data[u];
							}
							if(l2[movetol2.set].size()==v) {
								int min2 = 0;
								for(int y=0; y<l2[movetol2.set].size();y++) {
									if(l2[movetol2.set].get(y).time < l2[movetol2.set].get(min2).time) {
										min2 = y;
									}
								}
								int blocklost = todecimal(l2[movetol2.set].get(min2).tag + binaryconverter(l2[movetol2.set].get(min2).set,d2));
								l2[movetol2.set].remove(min2);
								l2[movetol2.set].add(movetol2);
								l1[s1].remove(minidx);
								l1[s1].add(obj);
								System.out.printf("L2: Replaced block no %d with block no %d\n", blocklost, todecimal(movetol2.tag + binaryconverter(movetol2.set,d2)));
							}
							else {
								l1[s1].remove(minidx);
								l1[s1].add(obj);
								l2[movetol2.set].add(movetol2);
								l2[s2].remove(i);
								System.out.printf("L2: Moved block no %d to cache\n",todecimal(movetol2.tag + binaryconverter(movetol2.set,d2)));
							}
							System.out.printf("L1: Moved block no %d to L2 cache\n", todecimal(movetol2.tag + binaryconverter(movetol2.set,d2)));	//redundant message but fine
							System.out.println("L1: Data written to cache successfully");
						}
						else {
							l1[s1].add(obj);
							l2[s2].remove(i);
							System.out.printf("L2: Moved block no %d to L1 cache\n", todecimal(obj.tag + binaryconverter(obj.set,d1)));
							System.out.println("L1: Data written to cache successfully");
						}
						break;
					}
					}
				}
			if(!foundin2) {
				System.out.println("L2: Cache Read Miss");
			}
		}
		
		public void writer(String address, int data) {
			String t1 = address.substring(0,n-w-d1);
			String t2 = address.substring(0,n-w-d2);
			String idx1 = address.substring(n-w-d1,n-w);
			String idx2 = address.substring(n-w-d2,n-w);
			String word = address.substring(n-w);
			int s1 = todecimal(idx1);
			int s2 = todecimal(idx2);
			gt += 1;
			boolean foundin1 = false;
			boolean foundin2 = false;
			for(int i =0; i<l1[s1].size(); i++) {
				if(l1[s1].get(i).tag.contentEquals(t1)) {
					foundin1 = true;
					System.out.println("L1: Cache Write Hit");
					l1[s1].get(i).dbit = 1;
					l1[s1].get(i).data[todecimal(word)] = data;
					l1[s1].get(i).set = s1;
					System.out.println("L1: Data updated in cache successfully");
					break;
				}
			}
			for(int j=0; j<l2[s2].size(); j++) {
				if(l2[s2].get(j).tag.contentEquals(t2)) {
					foundin2 = true;
					if(foundin1) {
						l2[s2].remove(j);
						System.out.println("must never reach here");
						break;
					}
					else {
						System.out.println("L1: Cache Write Miss");
						System.out.println("L2: Cache Write Hit");
						dmcache obj = new dmcache();
						obj.vbit = 1;
						obj.dbit = 1;
						obj.time = gt;
						obj.tag = t1;
						obj.set = s1;
						for(int u=0;u<B;u++) {
							obj.data[u] = l2[s2].get(j).data[u];
						}
						obj.data[todecimal(word)] = data;
						if(l1[s1].size() == v) {
							int minidx = 0;
							for(int y=0;y<l1[s1].size();y++) {
								if(l1[s1].get(y).time < l1[s1].get(minidx).time) {
									minidx = y;
								}
							}
							dmcache movetol2 = new dmcache();
							movetol2.vbit = 1;
							movetol2.dbit = l1[s1].get(minidx).dbit;
							movetol2.time = l1[s1].get(minidx).time;
							movetol2.tag = (l1[s1].get(minidx).tag + binaryconverter(s1,d1)).substring(0,n-w-d2);
							movetol2.set = todecimal((l1[s1].get(minidx).tag + binaryconverter(s1,d1)).substring(n-w-d2,n-w));
							for(int u=0;u<B;u++) {
								movetol2.data[u] = l1[s1].get(minidx).data[u];
							}
							l1[s1].add(obj);
							if(l2[movetol2.set].size()==v) {
								int min2 = 0;
								for(int y=0; y<l2[movetol2.set].size();y++) {
									if(l2[movetol2.set].get(y).time < l2[movetol2.set].get(min2).time) {
										min2 = y;
									}
								}
								int blocklost = todecimal(l2[movetol2.set].get(min2).tag + binaryconverter(l2[movetol2.set].get(min2).set,d2));
								l2[movetol2.set].remove(min2);	//uncommented this
								l2[movetol2.set].add(movetol2);
								l1[s1].remove(minidx);
								System.out.printf("L2: Replaced block no %d with block no %d\n", blocklost, todecimal(movetol2.tag + binaryconverter(movetol2.set,d2)));
							}
							else {
								l1[s1].remove(minidx);
								l2[movetol2.set].add(movetol2);
								l2[s2].remove(j);
								System.out.printf("L2: Moved block no %d to cache\n",todecimal(movetol2.tag + binaryconverter(movetol2.set,d2)));
							}
							System.out.printf("L1: Moved block no %d to L2 cache\n", todecimal(movetol2.tag + binaryconverter(movetol2.set,d2)));	//redundant message but fine
							System.out.println("L1: Data written to cache successfully");
						}
						else {
							l1[s1].add(obj);
							l2[s2].remove(j);
							System.out.printf("L2: Moved block no %d to L1 cache\n", todecimal(obj.tag + binaryconverter(obj.set,d1)));
							System.out.println("L1: Data written to cache successfully");
						}
						break;
					}
				}
			}
			if(!foundin1 && !foundin2) {
				System.out.println("L1: Cache Write Miss");
				System.out.println("L2: Cache Write Miss");
				dmcache obj = new dmcache();
				obj.vbit = 1;
				obj.dbit = 0;
				obj.time = gt;
				obj.tag = t1;
				obj.set = s1;
				obj.data[todecimal(word)] = data;
				if(l1[s1].size() == (v)) {
					int minidx = 0;
					for(int k=0;k<l1[s1].size();k++) {
						if(l1[s1].get(k).time < l1[s1].get(minidx).time) {
							minidx = k;
						}
					}
					int blockreplaced = todecimal(l1[s1].get(minidx).tag + binaryconverter(l1[s1].get(minidx).set,d1));
					dmcache moved = new dmcache();
					moved.vbit = 1;
					moved.dbit = l1[s1].get(minidx).dbit;
					moved.tag = (l1[s1].get(minidx).tag + binaryconverter(s1,d1)).substring(0,n-w-d2);
					moved.set = todecimal((l1[s1].get(minidx).tag + binaryconverter(s1,d1)).substring(n-w-d2,n-w));
					moved.time = l1[s1].get(minidx).time;
					for(int u=0;u<B;u++) {
						moved.data[u] = l1[s1].get(minidx).data[u];
					}
					l1[s1].remove(minidx);
					l1[s1].add(obj);
					System.out.println("L1: Data written to cache successfully");
					System.out.printf("L1: Moved block no %d to L2 cache\n", blockreplaced);
					if(l2[moved.set].size() == v) {
						int min2 = 0;
						for(int k=0;k<l2[moved.set].size();k++) {
							if(l2[moved.set].get(k).time < l2[moved.set].get(min2).time) {
								min2 = k;
							}
						}
						int blocklost = todecimal(l2[moved.set].get(min2).tag + binaryconverter(l2[moved.set].get(min2).set,d2));
//						System.out.println("block lost " + blocklost);
						l2[moved.set].remove(min2);
						//changed blocklost to min2 above line
						l2[moved.set].add(moved);
						System.out.printf("L2: Replaced block no %d with block no %d\n", blocklost, todecimal(moved.tag + binaryconverter(moved.set,d1)));
					}
					else {
						l2[moved.set].add(moved);				
					}
				}
				else {
					l1[s1].add(obj);
					System.out.println("L1: Data written to cache successfully");
				}
			}
		}
	}
	
		public static int powerofII(long n) {
			n = Math.abs(n);
			int p = (int)Math.ceil(Math.log(n)/Math.log(2));
			return p;
		}
	
		public static String binaryconverter(int n, int len) {
			String binaryint = Integer.toBinaryString(n);
			return String.format("%1$" + (len) + "s", binaryint).replace(' ', '0');
		}
		
		public static int todecimal(String num) {
			return Integer.parseInt(num,2);
		}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner read = new Scanner(System.in);
		System.out.println("STARTING CACHE SIMULATION");
		int i = 1;
		while(i!=0) {
			System.out.println("Choose mapping for cache:");
			System.out.println("1. Direct Mapping");
			System.out.println("2. Associative Memory Mapping");
			System.out.println("3. n-way Set Associative Memory Mapping");
			System.out.println("4. Exit");
			int choiceofmap = read.nextInt();
			if(choiceofmap == 1) {
				System.out.println("Direct Mapping");
				//Direct Mapping
				System.out.println("Enter size of main memory M (in words):");
				long M = read .nextLong();
				System.out.println("Enter number of cache lines CL:");
				long CL = read.nextLong();
				System.out.println("Enter block size B (in words):");
				long B = read.nextLong();
				Direct dmap = new Direct(M,CL,B);
				long add_bits = dmap.n;
				int j = 1;
				while(j!=0) {
					System.out.println("Choose operation");
					System.out.println("1. Read from cache");
					System.out.println("2. Write to cache");
					System.out.println("3. Back");
					System.out.println("4. Exit");
					int choiceofop = read.nextInt();
					if(choiceofop == 1) {
						System.out.printf("Enter %d - bit address to read word from:\n",add_bits);
						String address = read.next();
						dmap.reader(address);
						if(CL<=64) {
							System.out.println("L1 cache structure");
							dmap.l1printer();
							System.out.println();
							System.out.println("L2 cache structure");
							dmap.l2printer();					
						}
					}
					else if(choiceofop == 2) {
						System.out.printf("Enter %d - bit address to write to:\n",add_bits);
						String address = read.next();
						if((address + "").length()!=add_bits) {
							System.out.println("Enter valid data");
							return;
						}
						System.out.println("Enter data to be written to address:");
						int deets = read.nextInt();
						dmap.writer(address,deets);
						if(CL<=64) {
							System.out.println("L1 cache structure");
							dmap.l1printer();
							System.out.println();
							System.out.println("L2 cache structure");
							dmap.l2printer();					
						}
					}
					else if(choiceofop == 3) {
						j = 0;
					}
					else if(choiceofop == 4) {
						System.out.println("Exiting...");
						System.out.println("END");
						j = 0;
						return;
					}
					else {
						System.out.println("Enter valid choice");
					}
				}
			}
			else if(choiceofmap == 2) {
				System.out.println("Associative Memory Mapping");
				//Fully Associative Mapping
				System.out.println("Enter size of main memory M (in words):");
				long M = read.nextLong();
				System.out.println("Enter number of cache lines CL:");
				long CL = read.nextLong();
				System.out.println("Enter block size B (in words):");
				long B = read.nextLong();
				Associative amap = new Associative(M,CL,B);
				long add_bits = amap.n;
				int j = 1;
				while(j!=0) {
					System.out.println("Choose operation");
					System.out.println("1. Read from cache");
					System.out.println("2. Write to cache");
					System.out.println("3. Back");
					System.out.println("4. Exit");
					int choiceofop = read.nextInt();
					if(choiceofop == 1) {
						System.out.printf("Enter %d - bit address to read from:\n",add_bits);
						String address = read.next();
						amap.reader(address);
						if(CL<=64) {
							System.out.println("L1 cache structure");
							amap.l1printer();
							System.out.println();
							System.out.println("L2 cache structure");
							amap.l2printer();					
						}
					}
					else if(choiceofop == 2) {
						System.out.printf("Enter %d - bit address to write to:\n",add_bits);
						String address = read.next();
						if((address + "").length()!=add_bits) {
							System.out.println("Enter valid data");
							return;
						}
						System.out.println("Enter data to be written to address:");
						int deets = read.nextInt();
						amap.writer(address,deets);
						if(CL<=64) {
							System.out.println("L1 cache structure");
							amap.l1printer();
							System.out.println();
							System.out.println("L2 cache structure");
							amap.l2printer();					
						}
					}
					else if(choiceofop == 3) {
						j = 0;
					}
					else if(choiceofop == 4) {
						System.out.println("Exiting...");
						System.out.println("END");
						j = 0;
						return;
					}
					else {
						System.out.println("Enter valid choice");
					}
				}
			}
			else if(choiceofmap == 3) {
				System.out.println("n-way Set Associative Memory Mapping");
				//n-way Set Associative Memory
				System.out.println("Enter size of main memory M (in words):");
				long M = read.nextLong();
				System.out.println("Enter number of cache lines CL:");
				long CL = read.nextLong();
				System.out.println("Enter block size B (in words):");
				long B = read.nextLong();
				System.out.println("Enter n for n-way set associative memory:");
				int n = read.nextInt();
				SetAssociative smap = new SetAssociative(M,CL,B,n);
				long add_bits = smap.n;
				int j = 1;
				while(j!=0) {
					System.out.println("Choose operation");
					System.out.println("1. Read from cache");
					System.out.println("2. Write to cache");
					System.out.println("3. Back");
					System.out.println("4. Exit");
					int choiceofop = read.nextInt();
					if(choiceofop == 1) {
						System.out.printf("Enter %d - bit address to read word from:\n",add_bits);
						String address = read.next();
						smap.reader(address);
						if(CL<=64) {
							System.out.println("L1 cache structure");
							smap.l1printer();
							System.out.println();
							System.out.println("L2 cache structure");
							smap.l2printer();					
						}
					}
					else if(choiceofop == 2) {
						System.out.printf("Enter %d - bit address to write to:\n",add_bits);
						String address = read.next();
						if((address + "").length()!=add_bits) {
							System.out.println("Enter valid data");
							return;
						}
						System.out.println("Enter data to be written to address:");
						int deets = read.nextInt();
						smap.writer(address,deets);
						if(CL<=64) {
							System.out.println("L1 cache structure");
							smap.l1printer();
							System.out.println();
							System.out.println("L2 cache structure");
							smap.l2printer();					
						}
					}
					else if(choiceofop == 3) {
						j = 0;
					}
					else if(choiceofop == 4) {
						System.out.println("Exiting...");
						System.out.println("END");
						j = 0;
						return;
					}
					else {
						System.out.println("Enter valid choice");
					}
				}
			}
			else if(choiceofmap == 4) {
				System.out.println("Exiting...");
				System.out.println("END");
				i = 0;
				return;
			}
			else {
				System.out.println("Enter valid choice");
			}
		}
	}

}

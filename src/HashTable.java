//////////////////////////// Assignment Header ///////////////////////////////
//
//Title: CS 400 Assignment 3 Hash Table and Preformance Comparison
//Files: HashTable.java
//		  
//
//Course: CS 400, Spring, 2018
//
//Author: Christopher Todd Hayes-Birchler, Mostafa Wail Hassan
//Email: hayesbirchle@wisc.edu, mwhassan@wisc.edu
//Lecturer's Name: Deb Deppeler
//Due Date : 
//
///////////////////////////////// KNOWN BUGS //// /////////////////////////////
//
//	Calc Primes - https://www.tutorialspoint.com/java/math/biginteger_nextprobableprime.htm
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
//Course provided outlines.  Some comments remain from ADT or outline
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////


import java.math.BigInteger;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Implementation of HashTable with  map<k,v> type node and linked list buckets for dealing with
 * collisions
 * 
 * @author hayesbirchle
 *
 * @param <K> - Key object type
 * @param <V> - Value object type
 */
public class HashTable<K, V> implements HashTableADT<K, V> {
    /************************
     *Constants
     ***********************/
    private static final double DEF_LOAD_FACTOR = 0.75; //Default load factor for increasing 
                                                        //array size if not sent in
    private static final int DEF_CAPACITY = 31; //Def table size if not sent in
    
    
    /************************
     * Class Fields
     ***********************/
    LinkedList<Node>[] hashTable;
    double maxLoadFactor; //Load factor at which we resize the table when hit
    int initialCapacity; //Start capacity (start number of elements in array)
    int currentCapacity; //Current capacity (current number of elements in array
    int size; //current number of keys in hash table (TABLE_SIZE)
    int dupEntries; //Tracks number of duplicate put attempts for testing purposes
    
    
    
    /************************
     * Constructors
     ***********************/
    
    /**
     * Basic no arg uses defaults for load factor and start capacity
     */
    public HashTable(){
        this(DEF_CAPACITY, DEF_LOAD_FACTOR);
    }
    
    
    /**
     * Arg based constructor allowing for start capacity and max load factor to be sent in
     * @param initialCapacity - start table size
     * @param loadFactor - max load factor (table resizes when hit)
     */
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity, double loadFactor){
        this.initialCapacity = initialCapacity;
        currentCapacity = this.initialCapacity;
        this.maxLoadFactor = loadFactor;
        size = 0;
        
        hashTable = (LinkedList<HashTable<K, V>.Node>[]) new LinkedList[initialCapacity];
    }
    
    /************************
     * Public Interface
     ***********************/
    
    /**
     * Adds key and associated value to hash table
     *  1) Find hash index
     *  2) Check if key already exists 
     *  3) this will be our return value
     *  
     * @param key : The key that goes into the hashtable
     * @param value: The Value associated with the key
     * @return null if new value, value previously associated with key if overwritting current key
     * @throws NullPointerException - thrown if attempting to add null key
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public V put(K key, V value) throws NullPointerException {
        if(key == null) throw new NullPointerException("Cannot insert null key.");

        int hashIndex = hashFxn(key);


        V returnValue = null;
        
        //If no LL associated with index, add it
        if(hashTable[hashIndex] == null)
            hashTable[hashIndex] = (LinkedList<HashTable<K, V>.Node>) new LinkedList();
        else for (Node x : hashTable[hashIndex]){
            //Otherwise Identify if key is already in table, add and return old value if it is
            if (x.key.equals(key)){
                returnValue = x.value;
                x.setValue(value);
                dupEntries++;
                return returnValue;
            }
        }
        
        //Otherwise add new key and increment size
        hashTable[hashIndex].add(new Node(key, value));
        size++;
        
        //If load factor > max load factor then time to expand and reHash
        if (calcLF() > maxLoadFactor){
        	hashTable = reHash(hashTable);
        }
        return returnValue;
    }
    
    /**
     * Clears out the entire hash table
     */
    @SuppressWarnings("unchecked")
	@Override
    public void clear() {
        //Just reinstantiate
        hashTable = (LinkedList<HashTable<K, V>.Node>[]) new LinkedList[hashTable.length];
    }

    
    /**
     * Gets the value associated with the key sent in but does not remove it from the list
     * 
     * @param key: The key associated with the value to be returned
     * @return - returns the value associated with the key sent int
     * @throws NoSuchElementException - thrown if key not found in table
     */
    @Override
    public V get(K key) {
       int hashIndex = hashFxn(key);
   
        for (Node x : hashTable[hashIndex]){
            if (x.key == key){
                return x.value;
            }
        };
        //If it gets to this point it did not find the item as expected
        throw new NoSuchElementException("No such key in table.");
    }

    /**
     * Returns if the hash table is empty (size = 0)
     * 
     * @return - True if the size of the hash table is 0
     */
    @Override
    public boolean isEmpty() {
        return (size==0);
    }

    /**
     * Returns the value of the key sent in and removes it from the list or null if not in list
     * @param key: The key we want to return value and remove from list
     * @return - null if key does not appear, value if key does appear
     * @throws - NullPointerException if key sent in is null
     */
    @Override
    public V remove(K key) {
        if(key == null) throw new NullPointerException("Cannot remove null key.");
        
        int hashIndex = hashFxn(key);
        V returnValue = null;
        //Identify if key is already in table, return value if it is
        for (Node x : hashTable[hashIndex]){
            if (x.key == key){
                returnValue = x.value;
                hashTable[hashIndex].remove(x); 
                size--;
                return returnValue;
            }
        }
        
        return null;
    }

    /**
     * Returns current size of has table
     * @return - current size of hash table (number of items, not array length)
     */
    @Override
    public int size() {
        return size;
    }
    
    
    /**
     * Prints out hash table showing array index and k,v pairs.  For testing purposes
     * @return - string representing visulization of array
     */
    @Override
    public String toString(){
    	String mReturn = "";
    	for (int i = 0; i < hashTable.length; i++){
    		mReturn +=  "[" + i + "]";
    		if (hashTable[i]== null || hashTable[i].size()==0){
    			mReturn += "(i:0)-X";
    		} else {
    			mReturn += "(i:" + hashTable[i].size()+ "){k,v}";
	    		for (Node x: hashTable[i]) {
	    			mReturn += ("->" + x);
	    		}
    		}
    		mReturn += "\n";
    	}
    	
    	mReturn += "cLF: " + calcLF();
    	mReturn += "\ncCol: " + countCollisions() + "\n";
    	
    	return mReturn;
    }
    
    
    /************************
     * Protected testing methods
     ***********************/
    
    /**
     * Returns the number of collisions to help validate new hash fxn and rehash effeciency
     * @return - total number of collisions in hash table
     */
    protected int countCollisions(){
    	int mCount = 0;
    	
    	for (int i = 0; i < hashTable.length; i++){
    		if (hashTable[i] != null) mCount+= hashTable[i].size()-1;
    	}
    	
    	return mCount;
    }
    
    /**
     * Identifies largest depth within hash table
     * @return - largest depth of bucket found in hash table
     */
    protected int largestDepth(){
    	int maxDepth = 0;
    	
    	for (int i = 0; i < hashTable.length; i++){
    		if (hashTable[i] != null) {
    			if (maxDepth < hashTable[i].size()) maxDepth = hashTable[i].size();
    		}
    	}
    	return maxDepth;
    }
    
    /**
     * Returns current capacity
     * @return - current capacity of array (number of elements in hash table array)
     */
    protected int getCurrentCapacity(){
    	return this.currentCapacity;
    }
    
    /**
     * Returns number of attempted duplicate entries
     * @return - number of counted duplicate entreis
     */
    protected Integer countDupEntries(){
        return this.dupEntries;
    }

    
    /************************
     * Private Helper Methods
     ***********************/
    
    /**
     * Returns hash index for a given key
     * @param key - key we want to add to hash table
     * @return index - index where we should add the key to the hash table
     * 
     * Notes:
     * We tried a few different approaches including
     * 
     * 1)
     *  int hash1 = 0;
     *  int hash2 = 0;
     *  hash1 = Math.abs(key.hashCode());
     *  for (int i = 0; i < Integer.toString(hash1).length(); i++){
     *    hash2 += Integer.toString(hash1).charAt(i);
     *  }
     *  return ((hash1 + hash2*31)%hashTable.length);
     * 2)   
     *  return Math.abs(key.hashCode()%hashTable.length);
     * 3) 
     *  return Math.abs(((key.hashCode()+getNextPrime(hashTable.length))*31))%hashTable.length;
     *  
     *  in the end option 1 did a decent job of reducing collisions but significantly 
     *  increased run time performance.  The other two were not as efficient or as good at reducing
     *  collisions as the option we went with below
     */
    private int hashFxn(K key) {
    	int code = key.hashCode();
    	return (Math.abs(code*31)%hashTable.length);
    }
    
    
    /**
	 * Increases hash table array size and re-hashes current entries
	 * @param hashTable - table we want to rehash
	 * @return - new hash table
	 */
	@SuppressWarnings("unchecked")
	private LinkedList<HashTable<K, V>.Node>[] reHash(LinkedList<HashTable<K, V>.Node>[]  hashTable){
		HashTable mReturn = null;
		
		//Instantiate mReturn to the prime closes to two times the current size
		int newSize = getNextPrime(hashTable.length);
	
		
		mReturn = new HashTable(newSize, this.maxLoadFactor);
		currentCapacity = newSize;
		//Need to iterate through elements in array and re-Hash
	    for (int i = 0; i < hashTable.length; i++){	
	    	if(hashTable[i] != null)
		    	for (Node x : hashTable[i]){
		            mReturn.put(x.key, x.value);
		        }
	    }
		return mReturn.hashTable;
	}


	/**
	 * Calculates current load factor (number of items in hash table buckets/number of element 
     * found in the has table array)
	 * @return - current load factor 
	 */
	private double calcLF(){
		return (double) this.size/this.currentCapacity;
	}
	
	
	/**
	 * Calculates the next prime after doubling the current value
	 * 
	 * @param currentPrime - Current value.  Does not have to be a prime
	 * @return - The prime number that proceeds that current prime multiplied by two
	 * 
	 * Notes:
	 *     We played around with some other resizing options such as *4, but it proved 
	 *     in-effecient and lead to out of memory errors on larger input ranges
	 */
	private int getNextPrime(int currentPrime){
		//Yielded best run time with *2 and prime size tables.
		BigInteger nextPrime =  BigInteger.valueOf(currentPrime*2);
		nextPrime = nextPrime.nextProbablePrime();
		return nextPrime.intValue();
	}
	
	/*#########################################################
    # Internal Linked List Class
    #########################################################*/
    
    

	/**
	 * Node allows us to add key and value as mapped pair to linked list used in buckets
	 * (since linked list can only contain one object)
	 * @author hayesbirchle
	 *
	 */
	protected class Node {
        /************************
         * Class Fields
         ***********************/
        private V value;
        private K key;
        
        /************************
         * Constructors
         ***********************/
        
        public Node(K key, V value){
            this.key = key;
            this.value = value;
        }

        /************************
         * Getters and Setters
         ***********************/
        
        public V getValue() {
            return value;
        }

        public void setValue(V data) {
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }
        
        public String toString() {
            return "{" +  key.toString() + ", " + value.toString() + "}";
        }
        
    }
    
}

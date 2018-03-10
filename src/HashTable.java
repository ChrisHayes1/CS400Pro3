import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HashTable<K, V> implements HashTableADT<K, V> {
    /************************
     *Constants
     ***********************/
    
    private static final double DEF_LOAD_FACTOR = 0.75;
    private static final int DEF_CAPACITY = 31;
    
    /************************
     * Class Fields
     ***********************/
    
    LinkedList<Node>[] hashTable;
    double maxLoadFactor;
    int initialCapacity;
    int size;
    
    
    /************************
     * Constructors
     ***********************/
    
    
    public HashTable(){
        this(DEF_CAPACITY, DEF_LOAD_FACTOR);
    }
    
    
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity, double loadFactor){
        this.initialCapacity = initialCapacity;
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
     *  @throws NullPointerException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public V put(K key, V value) throws NullPointerException {
        if(key == null) throw new NullPointerException("Cannot insert null key.");
        
        int hashIndex = hashFxn(key);
        V returnValue = null;
        //Identify if key is already in table, return value if it is
        if(hashTable[hashIndex] == null)
            hashTable[hashIndex] = (LinkedList<HashTable<K, V>.Node>) new LinkedList();
        else for (Node x : hashTable[hashIndex]){
            if (x.key == key){
                returnValue = x.value;
                x.setData(value);
                return returnValue;
            }
        }
        
        hashTable[hashIndex].add(new Node(key, value));
        size++;
        return returnValue;
    }

    @Override
    public void clear() {
        hashTable = (LinkedList<HashTable<K, V>.Node>[]) new LinkedList[hashTable.length];
    }

    @Override
    public V get(K key) {
       int hashIndex = hashFxn(key);
        
        for (Node x : hashTable[hashIndex]){
            if (x.key == key){
                return x.value;
            }
        }
        
        throw new NoSuchElementException("No such key in table.");
    }

    @Override
    public boolean isEmpty() {
        return (size==0);
    }

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

    @Override
    public int size() {
        return size;
    }
    
    
    /************************
     * Private Helper Methods
     ***********************/
    
    private int hashFxn(K key) {
        return (key.hashCode()%hashTable.length);
    }
    
    
    /*#########################################################
    # Internal Linked List Class
    #########################################################*/
    
    protected class Node {
        /************************
         * Class Fields
         ***********************/
        private V value;
        private K key;
        
        /************************
         * Constructors
         ***********************/
        
        public Node(K key, V data){
            this.key = key;
            this.value = data;
        }

        /************************
         * Getters and Setters
         ***********************/
        
        public V getData() {
            return value;
        }

        public void setData(V data) {
            this.value = data;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }
        
        public String toString() {
            return "(" +  key.toString() + ", " + value.toString() + ")";
        }
        
    }
    
    public static void main(String[] args) {
        HashTable<String, Integer> table = new HashTable<String, Integer>(10, 0.05);
        table.put("hi", 4);
        table.put("bob", 6);
        table.put("this is not null", 17);
        System.out.println(table.remove("bob"));
        
        System.out.println(Arrays.toString(table.hashTable));
        
        System.out.println(table.put("hi", 60));
        
        System.out.println(Arrays.toString(table.hashTable));
    }
}

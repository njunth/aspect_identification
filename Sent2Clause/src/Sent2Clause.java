import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;


public class Sent2Clause {

	String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	LexicalizedParser lp;
	
	static boolean DEBUG = false;
	
	public Sent2Clause(){
		lp = LexicalizedParser.loadModel(parserModel);
	}
	
	private boolean isSplitter(String reln){
//		System.out.println(reln);
		return (reln.equals("conj") || reln.equals("appos") || reln.equals("parataxis"));
	}
	
	ArrayList<Collection<TypedDependency>> split2clauses(Collection<TypedDependency> tds){
		ArrayList<Collection<TypedDependency>> ans = new ArrayList<Collection<TypedDependency>>();
		
		LinkedList<TypedDependency> tdList = new LinkedList<TypedDependency>();
		tdList.addAll(tds);
		
		int root1 = 0;
		int root2 = 0;
		
		String rootWord2 = null;
		
		for(Iterator<TypedDependency> iter = tdList.iterator(); iter.hasNext();){
			TypedDependency td = iter.next();
			String reln = td.reln().toString();
			//if(reln.equals("conj") || reln.equals("appos") ){	// appositional or conjunct
			if(isSplitter(reln)){
				System.out.println(reln);
				root1 = td.gov().index();
				root2 = td.dep().index();
				rootWord2 = new String(td.dep().word());
				iter.remove();
				break;
			}
		}
		
		if(root2 == 0){		//no need to split
			ans.add(tds);
			return ans;
		}	
		
		//remove cc word
		for(Iterator<TypedDependency> iter = tdList.iterator(); iter.hasNext(); ){
			TypedDependency td = iter.next();
			if(td.reln().toString().equals("cc")){
				iter.remove();
				break;
			}
		}
		
		//extract subtrees 
		Collection<TypedDependency> subtree1 = extractSubtree(tdList, root1);
		Collection<TypedDependency> subtree2 = extractSubtree(tdList, root2);
		
		//check omitted subjective in subtree2
		for(Iterator<TypedDependency> iter = subtree1.iterator(); iter.hasNext();){
			TypedDependency td = iter.next();
			String reln = td.reln().toString();
			if(reln.equals("nsubj") || reln.equals("nsubjpass")){
				boolean flag = false;
				for(Iterator<TypedDependency> iter2 = subtree2.iterator(); iter2.hasNext();){
					TypedDependency tmpTd = iter2.next();
					String tmpReln = tmpTd.reln().toString();
					if(tmpReln.equals("nsubj") || tmpReln.equals("nsubjpass")){
						flag = true;
						break;
					}
				}
				if(flag) break;  // do nothing, because subtree2 has a subjective
				
				//else  add this subjective to subtree2
				IndexedWord newGov = new IndexedWord();
				newGov.setIndex(root2);
				newGov.setWord(rootWord2);
				IndexedWord newDep = new IndexedWord();	//the subjective
				newDep.setIndex(td.dep().index());
				newDep.setWord(td.dep().word());
				GrammaticalRelation newreln = td.reln();
				TypedDependency newTd = new TypedDependency(newreln, newGov, newDep);
				subtree2.add(newTd);
				
				int subjectiveIndex = td.dep().index();
				Collection<TypedDependency> subjDeps = copySubtree(subtree1, subjectiveIndex);
				for(Iterator<TypedDependency> iter1 = subjDeps.iterator(); iter1.hasNext();){
					subtree2.add(iter1.next());
				}
				break;
			}
		}
		
		for(Iterator<TypedDependency> iter = tdList.iterator(); iter.hasNext();){
			TypedDependency td = iter.next();
			if(td.dep().index() == root1){
				subtree1.add(td);
				IndexedWord newGov = new IndexedWord();
				newGov.setIndex(td.gov().index());
				if(td.gov().index() > 0){
					newGov.setWord(new String(td.gov().word()));
				}
				IndexedWord newDep = new IndexedWord();
				newDep.setIndex(root2);
				newDep.setWord(rootWord2);
				GrammaticalRelation newreln = td.reln();
				TypedDependency newTd = new TypedDependency(newreln, newGov, newDep);
				subtree2.add(newTd);
			}
			else{
				subtree1.add(td);
				subtree2.add(td);
			}
		}
		
		ArrayList<Collection<TypedDependency>> ans1 = split2clauses(subtree1);
		ArrayList<Collection<TypedDependency>> ans2 = split2clauses(subtree2);
		
		ans = merge(ans1, ans2);
		
		return ans;
		
	}
	
	private ArrayList<Collection<TypedDependency>> merge(
			ArrayList<Collection<TypedDependency>> ans1,
			ArrayList<Collection<TypedDependency>> ans2) {
		
		for(Iterator<Collection<TypedDependency>> iter = ans2.iterator(); iter.hasNext();){
			Collection<TypedDependency> tmp = iter.next();
			ans1.add(tmp);
		}
		return ans1;
	}

	private Collection<TypedDependency> extractSubtree(
			LinkedList<TypedDependency> tdList, int root) {
		Collection<TypedDependency> subtree = new ArrayList<TypedDependency>();
		Queue<Integer> queue = new ArrayDeque<Integer>();
		queue.add(root);
		while(!queue.isEmpty()){
			int govIndex = queue.poll();
			for(Iterator<TypedDependency> iter = tdList.iterator(); iter.hasNext();){
				TypedDependency td = iter.next();
				if(td.gov().index() == govIndex){
					subtree.add(td);
					queue.add(td.dep().index());
					iter.remove();
				}
			}
		}
		
		return subtree;
	}
	
	private Collection<TypedDependency> copySubtree(
			Collection<TypedDependency> subtree1, int root) {
		Collection<TypedDependency> subtree = new ArrayList<TypedDependency>();
		Queue<Integer> queue = new ArrayDeque<Integer>();
		queue.add(root);
		while(!queue.isEmpty()){
			int govIndex = queue.poll();
			for(Iterator<TypedDependency> iter = subtree1.iterator(); iter.hasNext();){
				TypedDependency td = iter.next();
				if(td.gov().index() == govIndex){
					subtree.add(td);
					queue.add(td.dep().index());
				}
			}
		}
		
		return subtree;
	}

	public Collection<String> cut(String sentence){
		
		Tree tree = lp.parse(sentence);
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		//Collection<TypedDependency> tds = gs.typedDependenciesCollapsed();
		Collection<TypedDependency> tds = gs.typedDependencies();
		
		if(DEBUG){
			Iterator<TypedDependency> iter = tds.iterator();
			while(iter.hasNext()){
				TypedDependency td = iter.next();
				System.out.println(String.format("gov: (%d, %s); reln:(%s), dep:(%d, %s)", td.gov().index(), td.gov().word(), td.reln().toString(), td.dep().index(), td.dep().word()));
			}
		}
		
		ArrayList<Collection<TypedDependency>> clauses_tds =  split2clauses(tds);
		Collection<String> clauses = new HashSet<String>();
		for(int i = 0; i < clauses_tds.size(); i++){
			clauses.add(deps2str(clauses_tds.get(i)));
		}
//		Collection<String> clauses = new HashSet<String>();
//		String temp []=sentence.split("[,.!?;]");
//		for(int i=0;i<temp.length;i++)
//			clauses.add(temp[i]);
		return clauses;
	}
	
	private String deps2str(Collection<TypedDependency> tds) {
		Comparator<TypedDependency> comparator = new Comparator<TypedDependency>(){

			@Override
			public int compare(TypedDependency o1, TypedDependency o2) {
				 return o1.dep().index() - o2.dep().index();
			}
		};
		
		ArrayList<TypedDependency> td_list = new ArrayList<TypedDependency>();
		td_list.addAll(tds);
		Collections.sort(td_list, comparator);
		
		String ans = "";
		for(Iterator<TypedDependency> iter = td_list.iterator(); iter.hasNext();){
			TypedDependency td = iter.next();
			ans += td.dep().word() + " ";
		}
		
		return ans;
	}

	public static void main(String[] args){
		Sent2Clause s2c = new Sent2Clause();
		//String sentence = "It takes a long time to get on the Web and to change Web Sites.";
		//sentence = "Nice laptop, powerful battery and easy to carry.";
		//sentence = "The lobster knuckles were ok, but pretty tasteless.";
		String sentence = "Fantastic for the price, but it is a pity keys were not illuminated.";
		System.out.println(sentence);
		Collection<String> clauses = s2c.cut(sentence);
		for(Iterator<String> iter = clauses.iterator(); iter.hasNext();){
			System.out.println(iter.next());
		}
	}
}

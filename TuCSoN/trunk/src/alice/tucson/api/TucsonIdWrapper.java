package alice.tucson.api;

public class TucsonIdWrapper<I> {

	private I id;
	
	public TucsonIdWrapper(I id){
		this.id = id;
	}
	
	public I getId(){
		return id;
	}
	
}

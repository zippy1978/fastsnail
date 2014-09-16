# Code faster on Android with Groovy and fastsnail !

**fastsnail** is a small Groovy library for Android, aiming at producing cleaner code, faster when building Android applications.

## Dependency injection

fastnail provides a lightweight dependency injection engine working at compile time (no overhead due to reflection).

To configure dependency injection:

### 1. Create a **config.json** file in **res/raw** folder of your application.

This file is responsible for configuring the application context.

**components** section is used to declare every component that can be injected.

Each componenent is then declared by it's name and must provide at least it's class name.

The optionnal *singleton* boolean property (default is false) can be set to ensure a single instance of the component is used by the whole application.

Here is an example:

	{
	    "components": {
	        "asyncManager": {
	            "className": "fr.grousset.fastsnail.async.AsyncManager",
	            "singleton": true
	        }
	    }
	}

### 2. Load the object graph

The object graph must be loaded at application startup. This can be done like this in the application class:

	public class MyApplication extends Application {
	
	    @Override
	    public void onCreate() {
	
	        // Load graph
	        ObjectGraph.load(R.raw.config, this)
	
	    }
	
	}
	
### 3. Inject components anywhere you need them

Use the **@InjectComponent** annotation:

	class HomeActivity extends Activity {

    	@InjectComponent AsyncManager mAsyncManager
    	
    	protected void onCreate(Bundle savedInstanceState) {

        	super.onCreate(savedInstanceState)
        	        	        	
        	mAsyncManager....
        }
    }
  
Name of the component can be provided as well if it does not match the field name (incuding the Android member naming convention starting with *m*) :

	@InjectComponent(name='asyncManager') AsyncManager mMyAsyncManager

## Views and layouts injection

fastsnail also provide annotations to inject views and layouts on activities and fragments...

On an Activity:

	@CompileStatic
	@InjectLayout(R.layout.activity_home)
	class HomeActivity extends Activity {
	
	    @InjectView(R.id.helloTextView) TextView mHelloTextView
	
	    protected void onCreate(Bundle savedInstanceState) {
	    
	    	// At this stage layout is set and views are injected
	    
	    	mHelloTextView.setText('Hello world from an Activity !!')
	    }
	}
	
On a Fragment:

	@InjectLayout(R.layout.fragment_home)
	public class HomeFragment extends Fragment {
	
	    @InjectView(R.id.contentTextView)
	    TextView mContentTextView
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	                             
	        // At this stage layout is set and views are injected
	
	        mContentTextView.setText('Hello from annotation !!!')
	
	    }
	}

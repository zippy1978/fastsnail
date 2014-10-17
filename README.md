# Code faster on Android with Groovy and fastsnail !

**fastsnail** is a small Groovy library for Android, aiming at producing cleaner code, faster when building Android applications.

## 1. Setup

The first step is to enable Groovy support and add the library to your **build.gradle** file.

Here is how to do:

```groovy
buildscript {
    repositories {
        mavenLocal()
        jcenter()
    }
    dependencies {
        // Groovy plugin
        classpath 'me.champeau.gradle:gradle-groovy-android-plugin:0.3.0'

        ...
    }
}

apply plugin: 'com.android.application'

android {
    ...
}

apply plugin: 'me.champeau.gradle.groovy-android'

repositories {
    jcenter()

    // fastsnail repository
    maven {
        url 'http://repo.grousset.fr/snapshots'
    }
}

...

dependencies {

    // Groovy version
    compile 'org.codehaus.groovy:groovy:2.4.0-beta-3:grooid'

    // fastsnail
    compile('fr.grousset.fastsnail:fastsnail:0.1-SNAPSHOT') {
        exclude group: 'org.codehaus.groovy' 
    }

    ...
}
```

## 2. Views and layouts injection

fastsnail provides annotations to inject views and layouts on activities and fragments.

#### On an Activity:

```groovy
@CompileStatic
@InjectLayout(R.layout.activity_home)
class HomeActivity extends Activity {

    @InjectView(R.id.hello_text_view) TextView helloTextView

    protected void onCreate(Bundle savedInstanceState) {
    
    	// At this stage layout is set and views are injected
    
    	helloTextView.setText('Hello world from an Activity !!')
    }
}
```
	
#### On a Fragment:

```groovy
@InjectLayout(R.layout.fragment_home)
public class HomeFragment extends Fragment {

    @InjectView(R.id.content_text_view) TextView contentTextView
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                             
        // At this stage layout is set and views are injected

        contentTextView.setText('Hello from a Fragment !!!')

    }
}
```

## 3. Dependency injection

After setting up a **config.json** file (see [Dependency Injection](https://github.com/zippy1978/fastsnail/wiki/Depedency-Injection) for details) like this:

```json
{
    "components": {
        "myManager": {
            "className": "fr.grousset.fastsnail.test.MyManager",
            "singleton": true
        }
    }
}
```

It is possible to inject any object as collaborator of another object, like this:

```groovy
class HomeActivity extends Activity {

    @InjectComponent MyManager myManager

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState)

        myManager.sayHello()
    }
}
```

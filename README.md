# RoomDatabaseLiveData

<h1>Android LiveData with Room Database tutorial</h1>
Before you Start this tutorial you need to check out previous Room database tutorials.
<ol>
 	<li><a href="http://thoughtnerds.com/android-room-persistence-library-database-tutorial/">Room Database</a></li>
 	<li><a href="http://thoughtnerds.com/join-queries-room-persistence-library/">JOIN queries in Room Database</a></li>
</ol>
<code>LiveData</code><span> is an observable data holder class. Unlike a regular observable, LiveData is lifecycle-aware, meaning it respects the lifecycle of other app components, such as activities, fragments, or services. This awareness ensures LiveData only updates app component observers that are in an active lifecycle state.</span>

LiveData considers an observer, which is represented by the<span> </span><code>Observer</code><span> </span>class, to be in an active state if its lifecycle is in the<span> </span><code>STARTED</code><span> </span>or<span> </span><code>RESUMED</code><span> </span>state. LiveData only notifies active observers about updates. Inactive observers registered to watch<span> </span><code>LiveData</code><span> </span>objects aren't notified about changes.

You can register an observer paired with an object that implements the<span> </span><code>LifecycleOwner</code><span> </span>interface. This relationship allows the observer to be removed when the state of the corresponding<span> </span><code>Lifecycle</code><span> </span>object changes to<span> </span><code>DESTROYED</code>. This is especially useful for activities and fragments because they can safely observe<span> </span><code>LiveData</code><span> </span>objects and not worry about leaks—activities and fragments are instantly unsubscribed when their lifecycles are destroyed.
<h3 id="the_advantages_of_using_livedata">The advantages of using LiveData</h3>
Using LiveData provides the following advantages:
<dl>
 	<dt><strong>Ensures your UI matches your data state</strong></dt>
 	<dd>LiveData follows the observer pattern. LiveData notifies<span> </span><code>Observer</code><span> </span>objects when the lifecycle state changes. You can consolidate your code to update the UI in these<span> </span><code>Observer</code><span> </span>objects. Instead of updating the UI every time the app data changes, your observer can update the UI every time there's a change.</dd>
 	<dt><strong>No memory leaks</strong></dt>
 	<dd>Observers are bound to<span> </span><code>Lifecycle</code><span> </span>objects and clean up after themselves when their associated lifecycle is destroyed.</dd>
 	<dt><strong>No crashes due to stopped activities</strong></dt>
 	<dd>If the observer's lifecycle is inactive, such as in the case of an activity in the back stack, then it doesn’t receive any LiveData events.</dd>
 	<dt><strong>No more manual lifecycle handling</strong></dt>
 	<dd>UI components just observe relevant data and don’t stop or resume observation. LiveData automatically manages all of this since it’s aware of the relevant lifecycle status changes while observing.</dd>
 	<dt><strong>Always up to date data</strong></dt>
 	<dd>If a lifecycle becomes inactive, it receives the latest data upon becoming active again. For example, an activity that was in the background receives the latest data right after it returns to the foreground.</dd>
 	<dt><strong>Proper configuration changes</strong></dt>
 	<dd>If an activity or fragment is recreated due to a configuration change, like device rotation, it immediately receives the latest available data.</dd>
 	<dt><strong>Sharing resources</strong></dt>
 	<dd>You can extend a<span> </span><code>LiveData</code><span> </span>object using the singleton pattern to wrap system services so that they can be shared in your app. The<span> </span><code>LiveData</code>object connects to the system service once, and then any observer that needs the resource can just watch the<span> </span><code>LiveData</code><span> </span>object. For more information.</dd>
 	<dd>First, we need to add following dependencies to your project</dd>
 	<dd>
<pre>implementation <span>"android.arch.lifecycle:extensions:1.1.0"
</span><span>// alternatively, just ViewModel
</span>implementation <span>"android.arch.lifecycle:viewmodel:1.1.0"
</span><span>// alternatively, just LiveData
</span>implementation <span>"android.arch.lifecycle:livedata:1.1.0"</span></pre>
</dd>
</dl>
1) Create a SchoolVIewModel Class to get school data.

&nbsp;
<pre><span>/**
</span><span> * View model class
</span><span> */
</span><span>public class </span>SchoolViewModel <span>extends </span>AndroidViewModel {
    <span>//live school data
</span><span>    </span><span>public final </span>LiveData&lt;List&lt;School&gt;&gt; <span>schoolLiveData</span><span>;
</span><span>    private </span>RoomDatabase <span>mDatabase</span><span>;
</span><span>
</span><span>    public </span><span>SchoolViewModel</span>(<span>@NonNull </span>Application application) {
        <span>super</span>(application)<span>;
</span><span>        </span><span>mDatabase </span>=RoomDatabase.<span>getDatabase</span>(<span>this</span>.getApplication())<span>;
</span><span>        this</span>.<span>schoolLiveData </span>= <span>mDatabase</span>.schoolDao().getAllSchoolsLive()<span>;
</span><span>    </span>}

    <span>public </span>LiveData&lt;List&lt;School&gt;&gt; <span>getSchoolLiveData</span>() {
        <span>return </span><span>schoolLiveData</span><span>;
</span><span>    </span>}
}</pre>
2) Create MainActivity.java
<pre><span>public class </span>MainActivity <span>extends </span>AppCompatActivity {
    <span>private static final </span>String <span>TAG </span>= <span>"MainActivity"</span><span>;
</span><span>    private </span>AppCompatTextView <span>mDataappCompatTextView</span><span>;
</span><span>    private </span>SchoolViewModel <span>mSchoolViewModel</span><span>;
</span><span>    private int </span><span>mCount </span>= <span>0</span><span>;
</span><span>
</span><span>    </span><span>@Override
</span><span>    </span><span>protected void </span><span>onCreate</span>(Bundle savedInstanceState) {
        <span>super</span>.onCreate(savedInstanceState)<span>;
</span><span>        </span>setContentView(R.layout.<span>activity_main</span>)<span>;
</span><span>        </span>RoomDatabase roomDatabase = RoomDatabase.<span>getDatabase</span>(MainActivity.<span>this</span>)<span>;
</span><span>        </span><span>//inserting sample data
</span><span>      //  DataInitializer.AddSampleDataAsync(roomDatabase);
</span><span>        </span><span>mDataappCompatTextView </span>= findViewById(R.id.<span>AM_data</span>)<span>;
</span><span>        </span><span>// showClassData(roomDatabase.classDao().getAllClass());
</span><span>
</span><span>        //  showStudentData(roomDatabase.studentDao().getAllStudents());
</span><span>        //  showSchoolData(roomDatabase.schoolDao().getAllSchools());
</span><span>        </span><span>/**
</span><span>         * Method to get data from multiple tables using join query*/
</span><span>//        List&lt;JoinSchoolClassStudentData&gt; joinSchoolClassStudentData = roomDatabase.schoolDao().getSchoolCLassDataWithStudents();
</span><span>//        showJoinData(joinSchoolClassStudentData);
</span><span>        </span>subscribeViewModel()<span>;
</span><span>    </span>}


    <span>private void </span><span>subscribeViewModel</span>() {
        <span>mSchoolViewModel </span>= ViewModelProviders.<span>of</span>(<span>this</span>).get(SchoolViewModel.<span>class</span>)<span>;
</span><span>        </span><span>mSchoolViewModel</span>.getSchoolLiveData().observe(MainActivity.<span>this, new </span>Observer&lt;List&lt;School&gt;&gt;() {
            <span>@Override
</span><span>            </span><span>public void </span><span>onChanged</span>(<span>@Nullable </span>List&lt;School&gt; schools) {
                Log.<span>d</span>(<span>TAG</span><span>, </span><span>"onChanged() called with: schools = [" </span>+ schools + <span>"]"</span>)<span>;
</span><span>                </span>showSchoolData(schools)<span>;
</span><span>            </span>}
        })<span>;
</span><span>    </span>}

    <span>public void </span><span>showJoinData</span>(List&lt;JoinSchoolClassStudentData&gt; joinSchoolClassStudentData) {
        StringBuilder sb = <span>new </span>StringBuilder()<span>;
</span><span>        for </span>(JoinSchoolClassStudentData joinSchoolClassStudentData1 : joinSchoolClassStudentData) {
            sb.append(String.<span>format</span>(Locale.<span>US</span><span>,
</span><span>                    </span><span>"School name :  %s, Class name : %s,</span><span>\n\n</span><span> Student list : %s </span><span>\n\n</span><span>"</span><span>, </span>joinSchoolClassStudentData1.getSchoolName()<span>, </span>joinSchoolClassStudentData1.getClassName()<span>, </span>joinSchoolClassStudentData1.getStudentDetails()))<span>;
</span><span>        </span>}
        <span>mDataappCompatTextView</span>.setText(String.<span>format</span>(<span>"</span><span>\n \n</span><span>%s%s"</span><span>, </span>sb.toString()<span>, </span><span>mDataappCompatTextView</span>.getText()))<span>;
</span><span>    </span>}

    <span>public void </span><span>showClassData</span>(List&lt;ClassStudent&gt; classStudentList) {
        StringBuilder sb = <span>new </span>StringBuilder()<span>;
</span><span>        for </span>(ClassStudent classStudent : classStudentList) {
            sb.append(String.<span>format</span>(Locale.<span>US</span><span>,
</span><span>                    </span><span>" %s : %s : %s </span><span>\n</span><span>"</span><span>, </span>classStudent.getClassId()<span>, </span>classStudent.getClassName()<span>, </span>classStudent.getClassDivision()))<span>;
</span><span>        </span>}
        <span>mDataappCompatTextView</span>.setText(String.<span>format</span>(<span>"</span><span>\n \n</span><span>%s%s"</span><span>, </span>sb.toString()<span>, </span><span>mDataappCompatTextView</span>.getText()))<span>;
</span><span>    </span>}

    <span>public void </span><span>showStudentData</span>(List&lt;Student&gt; studentList) {
        StringBuilder sb = <span>new </span>StringBuilder()<span>;
</span><span>        for </span>(Student student : studentList) {
            sb.append(String.<span>format</span>(Locale.<span>US</span><span>,
</span><span>                    </span><span>"%s : %s : %s </span><span>\n</span><span>"</span><span>, </span>student.getStudentId()<span>, </span>student.getStudentName()<span>, </span>student.getStudentAddress()))<span>;
</span><span>        </span>}
        <span>mDataappCompatTextView</span>.setText(String.<span>format</span>(<span>"</span><span>\n\n</span><span>%s%s"</span><span>, </span>sb.toString()<span>, </span><span>mDataappCompatTextView</span>.getText()))<span>;
</span><span>    </span>}


    <span>public void </span><span>showSchoolData</span>(List&lt;School&gt; schoolList) {
        StringBuilder sb = <span>new </span>StringBuilder()<span>;
</span><span>        for </span>(School school : schoolList) {
            sb.append(String.<span>format</span>(Locale.<span>US</span><span>,
</span><span>                    </span><span>"%s : %s : %s </span><span>\n</span><span>"</span><span>, </span>school.getSchoolId()<span>, </span>school.getSchoolName()<span>, </span>school.getSchoolAddress()))<span>;
</span><span>        </span>}
      <span>//  mDataappCompatTextView.setText(String.format("\n\n%s%s", sb.toString(), mDataappCompatTextView.getText()));
</span><span>        </span><span>mDataappCompatTextView</span>.setText( sb.toString())<span>;
</span><span>    </span>}

    <span>@Override
</span><span>    </span><span>protected void </span><span>onDestroy</span>() {
        <span>super</span>.onDestroy()<span>;
</span><span>        </span>unSubscribeSchoolLive()<span>;
</span><span>    </span>}

    <span>private void </span><span>unSubscribeSchoolLive</span>() {
        <span>if </span>(<span>mSchoolViewModel </span>!= <span>null </span>&amp;&amp; <span>mSchoolViewModel</span>.getSchoolLiveData().hasObservers()) {
            <span>mSchoolViewModel</span>.getSchoolLiveData().removeObservers(<span>this</span>)<span>;
</span><span>        </span>}
    }

    <span>public void </span><span>AddSchool</span>(View view) {
        <span>mCount</span>++<span>;
</span><span>        </span><span>mSchoolViewModel</span>.AddSchoolData(<span>mCount</span>)<span>;
</span><span>    </span>}
}</pre>
In onCreate, we subscribe the live view model
<pre><span>private void </span><span>subscribeViewModel</span>() {
    <span>mSchoolViewModel </span>= ViewModelProviders.<span>of</span>(<span>this</span>).get(SchoolViewModel.<span>class</span>)<span>;
</span><span>    </span><span>mSchoolViewModel</span>.getSchoolLiveData().observe(MainActivity.<span>this, new </span>Observer&lt;List&lt;School&gt;&gt;() {
        <span>@Override
</span><span>        </span><span>public void </span><span>onChanged</span>(<span>@Nullable </span>List&lt;School&gt; schools) {
            Log.<span>d</span>(<span>TAG</span><span>, </span><span>"onChanged() called with: schools = [" </span>+ schools + <span>"]"</span>)<span>;
</span><span>            </span>showSchoolData(schools)<span>;
</span><span>        </span>}
    })<span>;
</span>}</pre>
OnDestroy , we unsubscribe the live view model
<pre><span>private void </span><span>unSubscribeSchoolLive</span>() {
    <span>if </span>(<span>mSchoolViewModel </span>!= <span>null </span>&amp;&amp; <span>mSchoolViewModel</span>.getSchoolLiveData().hasObservers()) {
        <span>mSchoolViewModel</span>.getSchoolLiveData().removeObservers(<span>this</span>)<span>;
</span><span>    </span>}
}</pre>
4)Create main_activity.xml
<pre><span>&lt;?</span><span>xml version=</span><span>"1.0" </span><span>encoding=</span><span>"utf-8"</span><span>?&gt;
</span><span>&lt;android.support.constraint.ConstraintLayout </span><span>xmlns:</span><span>android</span><span>=</span><span>"http://schemas.android.com/apk/res/android"
</span><span>    </span><span>xmlns:</span><span>app</span><span>=</span><span>"http://schemas.android.com/apk/res-auto"
</span><span>    </span><span>xmlns:</span><span>tools</span><span>=</span><span>"http://schemas.android.com/tools"
</span><span>    </span><span>android</span><span>:layout_width=</span><span>"match_parent"
</span><span>    </span><span>android</span><span>:layout_height=</span><span>"match_parent"
</span><span>    </span><span>tools</span><span>:context=</span><span>"com.cretlabs.roomdatabase.MainActivity"</span><span>&gt;
</span><span>    &lt;android.support.v7.widget.AppCompatButton
</span><span>        </span><span>android</span><span>:layout_width=</span><span>"match_parent"
</span><span>        </span><span>android</span><span>:text=</span><span>"Add School"
</span><span>        </span><span>android</span><span>:onClick=</span><span>"AddSchool"
</span><span>        </span><span>android</span><span>:layout_height=</span><span>"wrap_content" </span><span>/&gt;
</span><span>    &lt;ScrollView
</span><span>        </span><span>android</span><span>:layout_margin=</span><span>"50dp"
</span><span>        </span><span>android</span><span>:layout_width=</span><span>"match_parent"
</span><span>        </span><span>android</span><span>:layout_height=</span><span>"match_parent"</span><span>&gt;
</span><span>        &lt;android.support.v7.widget.AppCompatTextView
</span><span>            </span><span>android</span><span>:id=</span><span>"@+id/AM_data"
</span><span>            </span><span>android</span><span>:padding=</span><span>"20dp"
</span><span>            </span><span>android</span><span>:layout_width=</span><span>"match_parent"
</span><span>            </span><span>android</span><span>:layout_height=</span><span>"wrap_content"
</span><span>            </span><span>app</span><span>:layout_constraintBottom_toBottomOf=</span><span>"parent"
</span><span>            </span><span>app</span><span>:layout_constraintLeft_toLeftOf=</span><span>"parent"
</span><span>            </span><span>app</span><span>:layout_constraintRight_toRightOf=</span><span>"parent"
</span><span>            </span><span>app</span><span>:layout_constraintTop_toTopOf=</span><span>"parent" </span><span>/&gt;
</span><span>    &lt;/ScrollView&gt;
</span><span>
</span><span>&lt;/android.support.constraint.ConstraintLayout&gt;

</span></pre>

<iframe width="560" height="315" src="https://www.youtube.com/embed/EQih-h0S8cg" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen="allowfullscreen"></iframe> 

&nbsp;
Originally published in http://thoughtnerds.com/android-livedata-room-database-tutorial/

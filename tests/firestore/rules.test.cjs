const {test,before,after,beforeEach}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const {initializeTestEnvironment,assertSucceeds,assertFails}=require('@firebase/rules-unit-testing');
const {doc,setDoc,getDoc,updateDoc,deleteDoc,collection,getDocs,serverTimestamp,Timestamp,writeBatch,runTransaction}=require('firebase/firestore');
let env;
before(async()=>{env=await initializeTestEnvironment({projectId:'demo-fit-rules',firestore:{host:'127.0.0.1',port:8087,rules:fs.readFileSync(path.join(__dirname,'../../firestore.rules'),'utf8')}})});
after(async()=>{if(env)await env.cleanup()});
beforeEach(async()=>env.clearFirestore());
const user=(uid='alice')=>env.authenticatedContext(uid,{email:uid+'@example.com'}).firestore();
const profile=(overrides={})=>({name:'Спортшы',email:'alice@example.com',age:25,height:170,weight:70,goal:'maintenance',createdAt:serverTimestamp(),...overrides});
const workout=(overrides={})=>({workoutId:'full',title:'Бүкіл дене',category:'full_body',durationMinutes:12.5,completedAt:serverTimestamp(),...overrides});
const weight=(overrides={})=>({weight:71,recordedAt:serverTimestamp(),...overrides});
async function create(){const db=user();await setDoc(doc(db,'users/alice'),profile());return db}
async function seedLegacy(){await env.withSecurityRulesDisabled(async c=>{const db=c.firestore();await setDoc(doc(db,'users/alice'),profile({goal:'Поддержание формы',createdAt:Timestamp.fromMillis(1000)}));await setDoc(doc(db,'users/alice/completedWorkouts/legacy'),workout({category:'Всё тело',completedAt:Timestamp.fromMillis(2000)}))})}
test('all stable goals are accepted, defaults may be zero',async()=>{for(const goal of ['weight_loss','muscle_gain','maintenance','endurance']){await env.clearFirestore();await assertSucceeds(setDoc(doc(user(),'users/alice'),profile({goal,age:0,height:0,weight:0})))}});
test('all stable categories are accepted',async()=>{const db=await create();for(const category of ['chest','back','legs','arms','shoulders','abs','cardio','full_body'])await assertSucceeds(setDoc(doc(db,'users/alice/completedWorkouts/'+category),workout({category})))});
test('unauthenticated and foreign reads/writes are denied',async()=>{await create();for(const db of [user('bob'),env.unauthenticatedContext().firestore()]){for(const p of ['users/alice','users/alice/completedWorkouts/x','users/alice/weightHistory/x']){await assertFails(getDoc(doc(db,p)));await assertFails(setDoc(doc(db,p),profile()))}}});
test('wrong email, missing fields, extra fields and wrong types are denied',async()=>{for(const change of [{email:'other@example.com'},{age:'25'},{age:25.5},{height:'170'},{weight:'70'},{goal:12},{admin:true},{name:'A'},{height:99},{weight:401},{age:101}])await assertFails(setDoc(doc(user(),'users/alice'),profile(change)));const p=profile();delete p.goal;await assertFails(setDoc(doc(user(),'users/alice'),p))});
test('new Russian, Kazakh and unknown goals are rejected',async()=>{for(const goal of ['Похудение','Салмақ тастау','unknown'])await assertFails(setDoc(doc(user(),'users/alice'),profile({goal})))});
test('profile save can migrate legacy goal and preserve metadata',async()=>{await seedLegacy();const db=user();const before=(await getDoc(doc(db,'users/alice'))).data();await assertSucceeds(updateDoc(doc(db,'users/alice'),{goal:'weight_loss',name:'Айдана'}));const after=(await getDoc(doc(db,'users/alice'))).data();assert.equal(after.createdAt.toMillis(),before.createdAt.toMillis());assert.equal(after.email,before.email)});
test('legacy history remains readable and immutable',async()=>{await seedLegacy();const db=user();const r=await assertSucceeds(getDoc(doc(db,'users/alice/completedWorkouts/legacy')));assert.equal(r.data().category,'Всё тело');await assertSucceeds(getDocs(collection(db,'users/alice/completedWorkouts')));await assertFails(updateDoc(doc(db,'users/alice/completedWorkouts/legacy'),{category:'full_body'}));await assertFails(deleteDoc(doc(db,'users/alice/completedWorkouts/legacy')))});
test('weight transaction migrates legacy profile without losing history',async()=>{await seedLegacy();const db=user();await assertSucceeds(runTransaction(db,async tx=>{const ref=doc(db,'users/alice');await tx.get(ref);tx.update(ref,{weight:72,goal:'maintenance'});tx.set(doc(db,'users/alice/weightHistory/new'),weight({weight:72}))}));assert.equal((await getDoc(doc(db,'users/alice'))).data().goal,'maintenance');assert.equal((await getDoc(doc(db,'users/alice/completedWorkouts/legacy'))).exists(),true)});
test('profile email, creation time and deletion are protected',async()=>{const db=await create();for(const change of [{email:'bob@example.com'},{createdAt:Timestamp.fromMillis(1000)},{goal:'Поддержание формы'}])await assertFails(updateDoc(doc(db,'users/alice'),change));await assertFails(deleteDoc(doc(db,'users/alice')))});
test('timestamps must be server timestamps, unknown paths denied',async()=>{await assertFails(setDoc(doc(user(),'users/alice'),profile({createdAt:Timestamp.fromMillis(1000)})));const db=await create();await assertFails(setDoc(doc(db,'users/alice/completedWorkouts/fake'),workout({completedAt:Timestamp.fromMillis(1000)})));await assertFails(setDoc(doc(db,'users/alice/weightHistory/fake'),weight({recordedAt:Timestamp.fromMillis(1000)})));await assertFails(setDoc(doc(db,'public/anything'),{x:true}))});
test('workout invalid fields, category and duration denied',async()=>{const db=await create();for(const change of [{category:'Грудь'},{category:'Кеуде'},{category:'unknown'},{category:1},{durationMinutes:0},{durationMinutes:-1},{durationMinutes:1441},{durationMinutes:'25'},{durationMinutes:NaN},{durationMinutes:Infinity},{title:''},{workoutId:''},{extra:true}])await assertFails(setDoc(doc(db,'users/alice/completedWorkouts/invalid'),workout(change)));const w=workout();delete w.title;await assertFails(setDoc(doc(db,'users/alice/completedWorkouts/missing'),w))});
test('workout retry transaction is idempotent and repeat sessions are distinct',async()=>{const db=await create();const ref=doc(db,'users/alice/completedWorkouts/session1');for(let i=0;i<2;i++)await assertSucceeds(runTransaction(db,async tx=>{if(!(await tx.get(ref)).exists())tx.set(ref,workout())}));await assertSucceeds(setDoc(doc(db,'users/alice/completedWorkouts/session2'),workout()));assert.equal((await getDocs(collection(db,'users/alice/completedWorkouts'))).size,2);await assertFails(updateDoc(ref,{durationMinutes:20}));await assertFails(deleteDoc(ref))});
test('weight types, bounds, extras and mutation denied',async()=>{const db=await create();for(const v of [19,401,'70',NaN,Infinity])await assertFails(setDoc(doc(db,'users/alice/weightHistory/invalid'),weight({weight:v})));await assertFails(setDoc(doc(db,'users/alice/weightHistory/extra'),weight({extra:1})));const ref=doc(db,'users/alice/weightHistory/valid');await assertSucceeds(setDoc(ref,weight()));await assertFails(updateDoc(ref,{weight:72}));await assertFails(deleteDoc(ref))});
test('subcollections require existing owner profile',async()=>{const db=user();await assertFails(setDoc(doc(db,'users/alice/completedWorkouts/orphan'),workout()));await assertFails(setDoc(doc(db,'users/alice/weightHistory/orphan'),weight()))});

test('stored ID contract matches the Android model',()=>{
 const source=fs.readFileSync(path.join(__dirname,'../../app/src/main/java/com/example/fit/data/model/StoredIds.kt'),'utf8');
 const rules=fs.readFileSync(path.join(__dirname,'../../firestore.rules'),'utf8');
 for(const [property,field] of [['goals','goal'],['categories','category']]){
  const kotlin=source.match(new RegExp('val '+property+' = listOf\\(([^)]*)\\)'))[1].match(/"([a-z_]+)"/g).map(s=>s.slice(1,-1));
  const allowed=rules.match(new RegExp(field+' in \\[([^\\]]*)\\]'))[1].match(/'([a-z_]+)'/g).map(s=>s.slice(1,-1));
  assert.deepEqual(allowed,kotlin);
 }
});
test('authentication without email cannot create empty-email profile',async()=>{
 const db=env.authenticatedContext('no-email').firestore();
 await assertFails(setDoc(doc(db,'users/no-email'),profile({email:''})));
});

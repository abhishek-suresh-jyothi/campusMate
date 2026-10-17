const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_student_123";
const BOB_UID = "bob_student_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.doc(`users/${ALICE_UID}`).get());
  await assertFails(
    unauthDb.doc(`users/${ALICE_UID}`).set({
      id: ALICE_UID,
      fullName: "Alice Student",
      email: "alice@campus.edu",
    })
  );
});

test("Authenticated user: can create and read their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.doc(`users/${ALICE_UID}`).set({
      id: ALICE_UID,
      fullName: "Alice Student",
      email: "alice@campus.edu",
      college: "IIT Bombay",
      university: "Autonomous Institute",
      course: "B.Tech",
      branch: "Computer Science",
      semester: 5,
      streakDays: 14,
      cgpa: 8.74,
      isAiSubscribed: true,
      subscriptionPlan: "Campus AI Pro",
      subscriptionPrice: "₹49/month",
      subscriptionPeriod: "Monthly",
      customUpiId: "campusmate.pay@upi",
      customQrUri: "",
    })
  );

  const doc = await aliceDb.doc(`users/${ALICE_UID}`).get();
  assertSucceeds(Promise.resolve(doc));
});

test("Cross-user isolation: Bob cannot read or modify Alice's profile", async () => {
  const adminDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await adminDb.doc(`users/${ALICE_UID}`).set({
    id: ALICE_UID,
    fullName: "Alice Student",
    email: "alice@campus.edu",
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.doc(`users/${ALICE_UID}`).get());
  await assertFails(
    bobDb.doc(`users/${ALICE_UID}`).update({
      fullName: "Bob Hacked This",
    })
  );
});

test("Assignments: Alice can add assignments to her subcollection, Bob cannot access", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/assignments`).doc("1").set({
      id: 1,
      title: "DBMS Lab Experiment 5",
      subject: "Database Management Systems",
      dueDate: "Tomorrow, 5:00 PM",
      points: 20,
      priority: "High",
      isCompleted: false,
      description: "Submit SQL queries and ER diagram printout",
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection(`users/${ALICE_UID}/assignments`).get());
});

test("Study Notes: Alice can create notes, Bob cannot access", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/notes`).doc("101").set({
      id: 101,
      title: "Dijkstra Algorithm Cheatsheet",
      subject: "Algorithms",
      content: "Single-source shortest path algorithm using priority queue with O(E log V) complexity.",
      tags: "Graphs, Greedy",
      isAiGenerated: true,
      timestamp: Date.now(),
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.doc(`users/${ALICE_UID}/notes/101`).get());
});

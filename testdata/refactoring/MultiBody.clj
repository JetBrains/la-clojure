(fact
  "Better constructor works"
  (let [test-user (new-user "Mark" "Mandel" "email" "password")]
    (:id test-user) => truthy
    (:firstname test-user) => "Mark"
    (:lastname test-user) => "Mandel"
    (:password<caret> test-user) => "password"
    (:email test-user) => "email"
    (:photo test-user) => nil
    (:last-logged-in test-user) => nil
    )
  )
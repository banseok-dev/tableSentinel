use uuid;

pub struct Agentidentity{
    pub uuid: String
}

impl Agentidentity{
    fn new() -> Self{
        let uuid = uuid::Uuid::new_v4();
        let uuid = uuid.to_string();
        Self { uuid }
    }
    fn you_id(&self) {
        println!("it's your id {0}", self.uuid);
    }
}
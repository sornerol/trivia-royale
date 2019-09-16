from collections import namedtuple

class Player:
    def __init__(self, user_id):
        self.user_id = user_id
        updates = namedtuple("PlayerUpdates", "user_id name location last_active_date is_active")

    def load(self):
        pass
        # TODO- Attempt to load the  player with the userId

    def save(self):
        pass
        # TODO- 
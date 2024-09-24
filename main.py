# ща ебать напишу вам проектик лютый

class Data:
  def __init__(self, kakoito_value_ebat):
    self.value = kakoito_value_ebat


async def get_data():
  # тут кароч по блютус ебаште там по этим протоколам обд доставайте данные по машинке
  return Data(42)

def main():
  print("ща ебать")
  data = await get_data()
  print("вашей машине пиздец")
  print(data.value)


if __name__ == "__main__":
  main()
